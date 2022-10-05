package kef.technology.allinoneshopping;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import kef.technology.allinoneshopping.databinding.GridItemBinding;

public class GridItemAdapter extends RecyclerView.Adapter<GridItemAdapter.ViewHolder> implements Filterable {

    private final LazyImageLoader lazyImageLoader;
    private List<ListItem> filteredList = new ArrayList<>();
    protected List<ListItem> copiedList = new ArrayList<>(), itemsList;
    private final OnItemClickListener clickListener;

    public GridItemAdapter(@NonNull Context context, List<ListItem> itemsList, OnItemClickListener clickListener) {
        this.itemsList = itemsList;
        this.clickListener = clickListener;
        lazyImageLoader = new LazyImageLoader(context);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                if(TextUtils.isEmpty(constraint)){
                    filteredList = copiedList;
                }
                else {
                    List<ListItem> tempList = new ArrayList<>();
                    for (ListItem item : copiedList) {
                        if (item.getTitle().toLowerCase().contains(constraint))
                            tempList.add(item);
                    }
                    filteredList = tempList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                updateList((List<ListItem>)results.values);
            }
        };
    }

    protected void updateList(List<ListItem> newList){
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new ItemsUpdateUtil(itemsList, newList));
        itemsList = newList;
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(GridItemBinding.inflate(LayoutInflater.from(parent.getContext())));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ListItem item = itemsList.get(position);
        holder.titleVw.setText(item.getTitle());
        holder.imageView.setImageBitmap(lazyImageLoader.loadImage(item.getImageRes()));
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    protected class ViewHolder extends RecyclerView.ViewHolder{
        TextView titleVw; ImageView imageView;
        public ViewHolder(@NonNull GridItemBinding binding) {
            super(binding.getRoot());
            titleVw = binding.gridTitle;
            imageView = binding.gridImage;
            binding.getRoot().setOnClickListener(v -> clickListener.onItemClicked(itemsList.get(getAdapterPosition())));
        }
    }

    public interface OnItemClickListener{
        void onItemClicked(ListItem item);
    }

}
