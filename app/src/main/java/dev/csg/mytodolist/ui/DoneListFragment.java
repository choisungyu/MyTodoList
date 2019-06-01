package dev.csg.mytodolist.ui;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
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
import android.widget.Filter;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import dev.csg.mytodolist.R;
import dev.csg.mytodolist.databinding.ItemTodoListBinding;
import dev.csg.mytodolist.model.Todo;

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
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull DoneViewHolder holder, int position) {
            Todo item = mItems.get(position);
            // TODO : 데이터를 뷰홀더에 표시하시오
            holder.binding.setTodo(item);
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
