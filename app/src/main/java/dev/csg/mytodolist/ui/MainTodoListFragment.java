package dev.csg.mytodolist.ui;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dev.csg.mytodolist.MainViewModel;
import dev.csg.mytodolist.R;
import dev.csg.mytodolist.databinding.ItemTodoListBinding;
import dev.csg.mytodolist.model.Todo;
import dev.csg.mytodolist.repository.AppDatabase;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainTodoListFragment extends Fragment {

    private EditText mEditText;
    private String mTitle;
    private MainTodoListAdapter mAdapter;

    private ActionMode mActionMode;
    private View mFab;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713

    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_long_click, menu);
            mFab.setVisibility(View.GONE);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.check:

                    mode.finish();
                    return true;
                case R.id.share:

                    shareNote();
                    return true;
                case R.id.delete:

                    alertDialogNote(mode);
                    return true;

                default:
                    return false;

            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mAdapter.mSelectedModelItem.clear();
            // actionMode 꺼지면 보이고 켜지면 사라지게 하기
            mFab.setVisibility(View.VISIBLE);
            mAdapter.notifyDataSetChanged();
            mActionMode = null;
        }
    };

    private void alertDialogNote(ActionMode mode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("확실한가요?");
        builder.setMessage("작업을 삭제 하시겠습니까?");
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                AppDatabase.getInstance(MainTodoListFragment.this.requireActivity()).todoDao().deleteAll(
                        mAdapter.getSelectedList()
                );
                mActionMode.setTitle(mAdapter.getSelectedList().size() + "");
                mode.finish();
            }
        });
        builder.setNegativeButton("아니오", (dialog, id) -> {
            // User cancelled the dialog
            dialog.cancel();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private String getByIdTitle() {

        StringBuilder sb = new StringBuilder();
        for (Todo todo : mAdapter.getSelectedList()) {
            String title = todo.getTitle();

            sb.append("- ").append(title).append("\n");
        }
        return sb.substring(1, sb.length() - 1);
    }


    private void shareNote() {

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");

        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "할 일 : \n");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getByIdTitle());
        startActivity(Intent.createChooser(sharingIntent, "공 유"));
    }


    public MainTodoListFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setQueryHint("탐색");
        searchView.setIconified(false);
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });

//        searchView.setQueryHint("탐색");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.check:
                return true;
            case R.id.action_done_list:
                // todo : done_list_fragment 로 가는거

                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.action_mainTodoListFragment_to_doneListFragment);
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_todo_list, container, false);

        mFab = view.findViewById(R.id.fab);
        mFab.setOnClickListener(view1 -> {
            // TODO : 새작업 가기
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.action_mainTodoListFragment_to_newTaskFragment);
        });

        mEditText = view.findViewById(R.id.edit_text);
        mEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            } else {
                mTitle = mEditText.getText().toString();
                AppDatabase.getInstance(requireActivity()).todoDao().insertAll(
                        new Todo(mTitle)
                );

                mEditText.setText("");
            }
            return true;
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MobileAds.initialize(requireContext(), "ca-app-pub-8544040742728303~7451111542");

        AdView mAdView = view.findViewById(R.id.ad_View);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        MainViewModel mainTodoViewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel.class);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        mAdapter = new MainTodoListAdapter(new MainTodoListAdapter.OnItemClickedListener() {
            @Override
            public boolean onLongClicked(View view, int position, Todo model) {
                // 액션모드 진입중이면 롱클릭 취소 -> onClicked로 이벤트 전달

                // 만약 if 문 없으면 없으나 있으나 맨 처음에 부터 시작되는 로직 ( 진동 계속 먹음 )
//                if (mActionMode != null) {
//                    return false;
//                }

                // 액션모드 진입
                mActionMode = view.startActionMode(mActionModeCallback);
                // 현재 아이템 선택, 타이틀 변경
                mAdapter.setSelect(model, position);
                mActionMode.setTitle(mAdapter.getSelectedList().size() + "");

                return true;
            }

            @Override
            public void onClicked(int position, Todo model) {
                if (mActionMode != null) {
                    // 액션모드 진입 == LongClicked

                    // 현재 아이템 선택, 타이틀 변경
                    mAdapter.setSelect(model, position);
                    mActionMode.setTitle(mAdapter.getSelectedList().size() + "");

                    // 선택한 아이템 갯수가 0이면 액션모드 나감
                    if (mAdapter.getSelectedList().size() == 0) {
                        mActionMode.setTitle("");
                        mActionMode.finish();
                    }

                } else {
                    // 액션모드 진입 전

                    // 번들로 담아서 보내줘야 함.
                    Bundle bundle = new Bundle();
                    bundle.putInt("id", model.getId());

                    NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                    navController.navigate(R.id.action_mainTodoListFragment_to_updateTaskFragment, bundle);

                }
            }
        });

        recyclerView.setAdapter(mAdapter);

        // unchecked (boolean = 0) observing
        mainTodoViewModel.getMainTaskItems().observe(requireActivity(), new Observer<List<Todo>>() {
            @Override
            public void onChanged(List<Todo> todos) {
                mAdapter.setItems(todos);
            }
        });


    }

    private static class MainTodoListAdapter extends RecyclerView.Adapter<MainTodoListAdapter.MainViewHolder> implements Filterable {

        private List<Todo> mItems = new ArrayList<>();
        private List<Todo> mItemsFull;
        private Set<Todo> mSelectedModelItem = new HashSet<>();

        interface OnItemClickedListener {
            boolean onLongClicked(View view, int position, Todo model);

            void onClicked(int position, Todo model);
        }

        private OnItemClickedListener mListener;


        private MainTodoListAdapter(OnItemClickedListener listener) {
            mListener = listener;
        }


        // todo의 item_list 들을 꽂아주는 setter
        private void setItems(List<Todo> items) {
            this.mItems = items;
            mItemsFull = new ArrayList<>(mItems);
//            mItemsFull.addAll(mItems);
            notifyDataSetChanged();
        }

        // todo의 selected(set) 된 model 들만 remove 하거나 add 해주는 setter
        private void setSelect(Todo model, int position) {
            // model 이 들어있는가
            if (mSelectedModelItem.contains(model)) {
                mSelectedModelItem.remove(model);
            } else {
                mSelectedModelItem.add(model);
            }
            notifyItemChanged(position);
        }

        // delete 용
        private List<Todo> getSelectedList() {
            List<Todo> results = new ArrayList<>();
            for (Todo todo : mItems) {
                if (mSelectedModelItem.contains(todo)) {
                    results.add(todo);
                }
            }
            return results;
        }

        @NonNull
        @Override
        public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_todo_list, parent, false);

            final MainViewHolder viewHolder = new MainViewHolder(view);

            view.setOnClickListener(v -> {
                final Todo item = mItems.get(viewHolder.getAdapterPosition());
                mListener.onClicked(viewHolder.getAdapterPosition(), item);
            });
            view.setOnLongClickListener(v -> {
                final Todo item = mItems.get(viewHolder.getAdapterPosition());
                return mListener.onLongClicked(v, viewHolder.getAdapterPosition(), item);
            });

            CheckBox checkBox = view.findViewById(R.id.checkBox);
            checkBox.setOnClickListener(v -> {
                final Todo item = mItems.get(viewHolder.getAdapterPosition());
//                item.setDone(checkBox.isChecked());
                // 체크 된 애로 보는거(boolean = 0) => 바뀐것을 누구에게 알려줘야 함
                // item 에서 isDone 을 1로 만듦

                item.setDone(checkBox.isChecked());
//                Toast.makeText(view.getContext(), "" + AppDatabase.getInstance(view.getContext()).todoDao().getDoneTask().getValue(), Toast.LENGTH_SHORT).show();

                // query 가져와서 getInstance 해라
                AppDatabase.getInstance(view.getContext()).todoDao().update(item);// null


                // 체크된 애들 db 에 저장

//                checkBox.setChecked(false);
            });

            return viewHolder;
        }


        @Override
        public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {
            Todo item = mItems.get(position);
            holder.binding.setTodo(item);

            if (item.getDone()) {
                holder.binding.checkBox.setChecked(true);
            } else {
                holder.binding.checkBox.setChecked(false);
            }

            if (mSelectedModelItem.contains(item)) {
                holder.itemView.setBackgroundColor(Color.CYAN);
            } else {
                holder.itemView.setBackgroundColor(Color.WHITE);
            }
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        @Override
        public Filter getFilter() {
            return searchViewFilter;
        }

        private Filter searchViewFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Todo> filteredList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(mItemsFull);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();

                    for (Todo todo : mItemsFull) {
                        if (todo.getTitle().toLowerCase().contains(filterPattern)) {
                            filteredList.add(todo);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mItems.clear();
                mItems.addAll((Collection<? extends Todo>) results.values);
                notifyDataSetChanged();
            }
        };

        private static class MainViewHolder extends RecyclerView.ViewHolder {
            ItemTodoListBinding binding;

            private MainViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = DataBindingUtil.bind(itemView);
            }
        }
    }


}
