package net.pmellaaho.rxapp.ui;

import android.databinding.DataBindingUtil;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.pmellaaho.rxapp.model.Contributor;
import net.pmellaaho.rxapp.R;
import net.pmellaaho.rxapp.databinding.ListItemBinding;

import java.util.ArrayList;
import java.util.List;

public class ContributorsAdapter extends RecyclerView.Adapter<ContributorsAdapter.ViewHolder> {
    private List<Contributor> mDataset = new ArrayList<>();
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int pos);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ListItemBinding mBinding;

        public ViewHolder(final View view, final ListItemBinding binding) {
            super(view);
            mBinding = binding;
        }

        @UiThread
        public void bind(final Contributor contributor) {
            mBinding.setContributor(contributor);
            mBinding.setListener(this);
        }

        public void onItemClick(View view) {
            if (mListener != null) {
                mListener.onItemClick(getAdapterPosition());
            }
        }
    }

    @Override
    public ContributorsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final ListItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.list_item,
                parent, false);
        return new ViewHolder(binding.getRoot(), binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(mDataset.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void setData(List<Contributor> items) {
        mDataset.clear();
        mDataset.addAll(items);
        notifyDataSetChanged();
    }

    public void setListener(OnItemClickListener listener) {
        mListener = listener;
    }
}
