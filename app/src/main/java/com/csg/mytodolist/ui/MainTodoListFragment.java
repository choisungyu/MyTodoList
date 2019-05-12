package com.csg.mytodolist.ui;


import android.content.ClipData;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.csg.mytodolist.MainTodoViewModel;
import com.csg.mytodolist.R;
import com.csg.mytodolist.databinding.ItemTodoListBinding;
import com.csg.mytodolist.model.Todo;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainTodoListFragment extends Fragment {

    private static final String TAG = NewTaskFragment.class.getSimpleName();
    private List<Todo> itemList = new ArrayList<>();

    public MainTodoListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        itemList.add(new Todo("dd", "dd"));
        itemList.add(new Todo("dd", "dd"));
        itemList.add(new Todo("dd", "dd"));
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
                Toast.makeText(requireContext(), TAG, Toast.LENGTH_SHORT).show();
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
        final MainTodoListAdapter adapter = new MainTodoListAdapter();

        recyclerView.setAdapter(adapter);

        mainTodoViewModel.getItems().observe(this, new Observer<List<Todo>>() {
            @Override
            public void onChanged(List<Todo> todos) {
                adapter.setItems(itemList);

            }
        });
    }

    private static class MainTodoListAdapter extends RecyclerView.Adapter<MainTodoListAdapter.MainViewHolder> {

        private List<Todo> mItems = new ArrayList<>();

        interface OnItemClickedListener {
            void setOnClicked(Todo model);
        }

        private OnItemClickedListener mListener;


        public MainTodoListAdapter() {
        }

        public MainTodoListAdapter(OnItemClickedListener listener) {
            mListener = listener;
        }

        public void setItems(List<Todo> items) {
            this.mItems = items;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_todo_list, parent, false);
            final MainViewHolder viewHolder = new MainViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        final Todo item = mItems.get(viewHolder.getAdapterPosition());
                        mListener.setOnClicked(item);
                    }
                }
            });
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull MainViewHolder holder, int position) {
            Todo item = mItems.get(position);
            // TODO : 데이터를 뷰홀더에 표시하시오
            //holder.binding.setItemPhoto(item)
//            holder.titleTextView.setText(item.getTitle());
//            holder.timeTextView.setText(item.getContent());
            holder.binding.setTodo(item);
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public static class MainViewHolder extends RecyclerView.ViewHolder {
            // TODO : 뷰홀더 완성하시오
            ItemTodoListBinding binding;
//            private TextView titleTextView;
//            private TextView timeTextView;

            public MainViewHolder(@NonNull View itemView) {
                super(itemView);
                // TODO : 뷰홀더 완성하시오
                binding = DataBindingUtil.bind(itemView);
//                titleTextView = itemView.findViewById(R.id.text_view_title);
//                timeTextView = itemView.findViewById(R.id.text_view_time);
            }
        }
    }


}
