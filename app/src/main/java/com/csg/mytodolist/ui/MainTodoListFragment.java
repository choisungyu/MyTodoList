package com.csg.mytodolist.ui;


import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

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

import com.csg.mytodolist.MainTodoViewModel;
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

    public MainTodoListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
//                Toast.makeText(requireContext(), TAG, Toast.LENGTH_SHORT).show();
                NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.action_mainTodoListFragment_to_newTaskFragment);
            }
        });

        editText = view.findViewById(R.id.edit_text);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:

                        break;
                    default:
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

        //ViewModel-----------
        MainTodoViewModel mainTodoViewModel = ViewModelProviders.of(requireActivity()).get(MainTodoViewModel.class);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        mAdapter = new MainTodoListAdapter(new MainTodoListAdapter.OnItemClickedListener() {
            @Override
            public void setOnLongClicked(View view, int position, Todo model) {
                Toast.makeText(requireContext(), position + model.toString(), Toast.LENGTH_SHORT).show();
                mAdapter.setSelect(model);
                mAdapter.notifyItemChanged(position);

            }
        });

        recyclerView.setAdapter(mAdapter);

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
        private ActionMode mActionMode;

        interface OnItemClickedListener {
            void setOnLongClicked(View view, int position, Todo model);
        }

        private OnItemClickedListener mListener;


        private MainTodoListAdapter(OnItemClickedListener listener) {
            mListener = listener;
        }

        void setItems(List<Todo> items) {
            this.mItems = items;
            notifyDataSetChanged();
        }

        void setSelect(Todo model) {
            // model 이 들어있는가
            if (mSelectedModelItem.contains(model)) {
                mSelectedModelItem.remove(model);
            } else {
                mSelectedModelItem.add(model);
            }
        }

        @NonNull
        @Override
        public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_todo_list, parent, false);

            final MainViewHolder viewHolder = new MainViewHolder(view);

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mListener != null) {
                        final Todo item = mItems.get(viewHolder.getAdapterPosition());
                        mListener.setOnLongClicked(v, viewHolder.getAdapterPosition(), item);
                    }

                    if (mActionMode != null) {
                        return true;
                    }

                    if (mActionMode == null) {
                        mActionMode = view.startActionMode(mActionModeCallback);
                    }



                    return true;
                }
            });
            return viewHolder;
        }

        private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.menu_long_click, menu);
                mode.setTitle(mSelectedModelItem.size() + "");

                if (mSelectedModelItem.size() == 0) {
                    mActionMode.finish();
                    mActionMode = null;
                }
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.check:
//                        Toast.makeText(, "", Toast.LENGTH_SHORT).show();
                        mode.finish();
                        return true;
                    case R.id.share:
//                        Toast.makeText(, "", Toast.LENGTH_SHORT).show();
                        mode.finish();
                    case R.id.delete:
//                        Toast.makeText(, "", Toast.LENGTH_SHORT).show();
                        mode.finish();
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

        @Override
        public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {
            Todo item = mItems.get(position);
            // TODO : 데이터를 뷰홀더에 표시하시오
            holder.binding.setTodo(item);

            if (mSelectedModelItem.contains(item)) {
                holder.itemView.setBackgroundColor(Color.RED);
            } else {
                holder.itemView.setBackgroundColor(Color.WHITE);
            }
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public static class MainViewHolder extends RecyclerView.ViewHolder {
            // TODO : 뷰홀더 완성하시오
            ItemTodoListBinding binding;

            public MainViewHolder(@NonNull View itemView) {
                super(itemView);
                // TODO : 뷰홀더 완성하시오
                binding = DataBindingUtil.bind(itemView);
            }
        }
    }


}
