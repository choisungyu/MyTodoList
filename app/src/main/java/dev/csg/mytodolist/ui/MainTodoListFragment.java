package dev.csg.mytodolist.ui;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
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
//                        Toast.makeText(, "", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(requireContext(), "종료", Toast.LENGTH_SHORT).show();
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
        builder.setPositiveButton("예", (dialog, id) -> {
            // User clicked OK button
            AppDatabase.getInstance(requireActivity()).todoDao().deleteAll(
                    mAdapter.getSelectedList()
            );
            mActionMode.setTitle(mAdapter.getSelectedList().size() + "");
            mode.finish();
        });
        builder.setNegativeButton("아니오", (dialog, id) -> {
            // User cancelled the dialog
            dialog.cancel();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void shareNote() {
//        Bundle bundle = getArguments();
//        if (bundle != null) {
//
//            bundleInt = bundle.getInt("id");
        Todo todo = AppDatabase.getInstance(requireContext()).todoDao().getTodo();
        int id = todo.getId();

        Todo mTodo = AppDatabase.getInstance(requireContext()).todoDao().getTodoById(id);
        Toast.makeText(requireContext(), "" + mTodo.getTitle(), Toast.LENGTH_SHORT).show();


        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");

        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "할 일 : ");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, mTodo.getTitle());
        startActivity(Intent.createChooser(sharingIntent, "공 유"));
    }


    public MainTodoListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_todo_list, container, false);

        mFab = view.findViewById(R.id.fab);
//        mFab.setVisibility(View.VISIBLE);
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

//                if (mAdapter.getSelectedList().size() >= 1) {
//                    mFab.setVisibility(View.GONE);
//                } else {
//                    mFab.setVisibility(View.VISIBLE);
//                }


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
                        mActionMode.finish();
//                        mFab.setVisibility(View.VISIBLE);
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

        // 추가되던 지우던 값 갱신되서 알아서 꽂아주기만 하는 곳
        mainTodoViewModel.getItems().observe(requireActivity(), todos -> mAdapter.setItems(todos));
    }

    private static class MainTodoListAdapter extends RecyclerView.Adapter<MainTodoListAdapter.MainViewHolder> {

        private List<Todo> mItems = new ArrayList<>();
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
            List<Todo> result = new ArrayList<>();
            for (Todo todo : mItems) {
                if (mSelectedModelItem.contains(todo)) {
                    result.add(todo);
                }
            }
            return result;
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
            return viewHolder;
        }


        @Override
        public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {
            Todo item = mItems.get(position);
            holder.binding.setTodo(item);

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

        private static class MainViewHolder extends RecyclerView.ViewHolder {
            ItemTodoListBinding binding;

            private MainViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = DataBindingUtil.bind(itemView);
            }
        }
    }


}
