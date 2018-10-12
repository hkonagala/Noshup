package homeaway.com.placefinder.adapter;


import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.arlib.floatingsearchview.util.Util;

import java.util.ArrayList;
import java.util.List;

import homeaway.com.placefinder.R;
import homeaway.com.placefinder.model.venueModel.Venue;

/**
 * Adapter for search suggestions
 */
public class SuggestionsAdapter extends RecyclerView.Adapter<SuggestionsAdapter.ViewHolder> {

    // The application context for getting resources
    private Context context;

    // The list of results from the Foursquare API
    private List<Venue> results;

    //custom on item click listener
    public interface OnItemClickListener{
        void onClick(Venue venue);
    }

    private OnItemClickListener mItemsOnClickListener;
    private int mLastAnimatedItemPosition = -1;

    public SuggestionsAdapter(Context context) {
        this.context = context;
        results = new ArrayList<>();
    }

    public List<Venue> getResults() {
        return results;
    }

    public void setResults(List<Venue> results) {
        this.results = results;
        notifyDataSetChanged();
    }

    public void setItemsOnClickListener(OnItemClickListener onClickListener){
        this.mItemsOnClickListener = onClickListener;
    }


    @NonNull
    @Override
    public SuggestionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggestion_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionsAdapter.ViewHolder holder, int position) {
        // Sets each view with the appropriate venue details
        Venue venue = results.get(position);

        holder.suggestionName.setText(venue.getCategories().get(position).getName());

        if(mLastAnimatedItemPosition < position){
            animateItem(holder.itemView);
            mLastAnimatedItemPosition = position;
        }

        if(mItemsOnClickListener != null){
            holder.itemView.setOnClickListener(v -> mItemsOnClickListener.onClick(venue));
        }
    }

    private void animateItem(View itemView) {
        itemView.setTranslationY(Util.getScreenHeight((Activity) itemView.getContext()));
        itemView.animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator(3.f))
                .setDuration(700)
                .start();
    }

    @Override
    public int getItemCount() {
        List<Venue> venues = results;
        if (venues != null) {
            return venues.size();
        } else {
            return 0;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView suggestionName;

        ViewHolder(View itemView) {
            super(itemView);
            suggestionName = itemView.findViewById(R.id.suggestion_name);
        }
    }
}
