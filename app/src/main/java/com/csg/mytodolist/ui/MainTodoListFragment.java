package com.csg.mytodolist.ui;


import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.csg.mytodolist.MainViewModel;
import com.csg.mytodolist.R;
import com.csg.mytodolist.databinding.ItemTodoListBinding;
import com.csg.mytodolist.model.Todo;
import com.csg.mytodolist.repository.AppDatabase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainTodoListFragment extends Fragment {

    private EditText editText;
    private String mTitle;
    private MainTodoListAdapter mAdapter;

    private ActionMode mActionMode;

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_long_click, menu);
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
//                        Toast.makeText(, "", Toast.LENGTH_SHORT).show();
                    mode.finish();
                    return true;
                case R.id.delete:

                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setTitle("확실한가요?");
                    builder.setMessage("작업을 삭제 하시겠습니까?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button
                            AppDatabase.getInstance(requireActivity()).todoDao().deleteAll(
                                    mAdapter.getSelectedList()
                            );
                            mActionMode.setTitle(mAdapter.getSelectedList().size() + "");
                            mode.finish();
                        }
                    });
                    builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                            dialog.cancel();
                        }
                    });
                    AlertDialog dialog = builder.create();    // 알림창 객체 생성
                    dialog.show();
                    return true;

                default:
                    return false;

            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    };

    public MainTodoListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_todo_list, container, false);

        view.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO : 새작업 가기
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.action_mainTodoListFragment_to_newTaskFragment);
            }
        });

        editText = view.findViewById(R.id.edit_text);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                } else {
                    mTitle = editText.getText().toString();
                    AppDatabase.getInstance(requireActivity()).todoDao().insertAll(
                            new Todo(mTitle)
                    );

                    editText.setText("");
                }
                return true;
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
//                mActionMode.setTitle(mAdapter.getSelectedModelItemSize() + "");
                mActionMode.setTitle(mAdapter.getSelectedList().size() + "");


                return true;
            }

            @Override
            public void onClicked(int position, Todo model) {
                if (mActionMode != null) {
                    // 액션모드 진입 == LongClicked

                    // 현재 아이템 선택, 타이틀 변경
                    mAdapter.setSelect(model, position);
//                    mActionMode.setTitle(mAdapter.getSelectedModelItemSize() + "");
                    mActionMode.setTitle(mAdapter.getSelectedList().size() + "");

                    // 선택한 아이템 갯수가 0이면 액션모드 나감
                    if (mAdapter.getSelectedList().size() == 0) {
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

        // 추가되던 지우던 값 갱신되서 알아서 꽂아주기만 하는 곳
        mainTodoViewModel.getItems().observe(requireActivity(), new Observer<List<Todo>>() {
            @Override
            public void onChanged(List<Todo> todos) {
                mAdapter.setItems(todos);

            }
        });
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

        // 아예 안 쓰이게 되는거
        private int getSelectedModelItemSize() {
            return mSelectedModelItem.size();
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

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Todo item = mItems.get(viewHolder.getAdapterPosition());
                    mListener.onClicked(viewHolder.getAdapterPosition(), item);
                }
            });
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final Todo item = mItems.get(viewHolder.getAdapterPosition());
                    return mListener.onLongClicked(v, viewHolder.getAdapterPosition(), item);
                }
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

        public static class MainViewHolder extends RecyclerView.ViewHolder {
            ItemTodoListBinding binding;

            public MainViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = DataBindingUtil.bind(itemView);
            }
        }
    }


}
