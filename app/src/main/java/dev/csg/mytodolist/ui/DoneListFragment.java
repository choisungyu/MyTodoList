package dev.csg.mytodolist.ui;


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

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Adapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import dev.csg.mytodolist.MainViewModel;
import dev.csg.mytodolist.R;
import dev.csg.mytodolist.databinding.ItemTodoListBinding;
import dev.csg.mytodolist.model.Todo;
import dev.csg.mytodolist.repository.AppDatabase;

public class DoneListFragment extends Fragment {


    private DoneListAdapter mAdapter;

    public DoneListFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_done, menu);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_done_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        mAdapter = new DoneListAdapter();
        recyclerView.setAdapter(mAdapter);

        MainViewModel mainTodoViewModel = ViewModelProviders.of(requireActivity()).get(MainViewModel.class);


        // unchecked (boolean = 1) observing
        mainTodoViewModel.getDoneTaskItems().observe(requireActivity(), new Observer<List<Todo>>() {
            @Override
            public void onChanged(List<Todo> todos) {
                mAdapter.setItems(todos);
            }
        });
    }

    private static class DoneListAdapter extends RecyclerView.Adapter<DoneListAdapter.DoneViewHolder> {

        private List<Todo> mItems = new ArrayList<>();
        private List<Todo> mItemsFull;

        interface OnItemClickListener {
            void onClicked(Todo model);
        }

        private OnItemClickListener mListener;


        public DoneListAdapter() {
        }

        // listener 사용할 때, 사용하기
        public DoneListAdapter(OnItemClickListener listener) {
            mListener = listener;
        }

        public void setItems(List<Todo> items) {
            this.mItems = items;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public DoneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_todo_list, parent, false);
            final DoneViewHolder viewHolder = new DoneViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        final Todo item = mItems.get(viewHolder.getAdapterPosition());
                        mListener.onClicked(item);
                    }
                }
            });

            // doneListFragment 에서도 똑같이 눌렀을 때 상태 변화 체크
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
        public void onBindViewHolder(@NonNull DoneViewHolder holder, int position) {
            Todo item = mItems.get(position);
            // TODO : 데이터를 뷰홀더에 표시하시오
            holder.binding.setTodo(item);

            if (item.getDone()) {
                holder.binding.checkBox.setChecked(true);
            } else {
                holder.binding.checkBox.setChecked(false);
            }
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        private Filter getFilter() {
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


        private static class DoneViewHolder extends RecyclerView.ViewHolder {
            // TODO : 뷰홀더 완성하시오
            ItemTodoListBinding binding;

            private DoneViewHolder(@NonNull View itemView) {
                super(itemView);
                // TODO : 뷰홀더 완성하시오
                binding = DataBindingUtil.bind(itemView);
            }
        }
    }
}
