package homeaway.com.foodfinder.adapter;


import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.arlib.floatingsearchview.util.Util;

import java.util.ArrayList;
import java.util.List;

import homeaway.com.foodfinder.R;
import homeaway.com.foodfinder.model.venueModel.Venue;

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

    public SuggestionsAdapter(Context context/*, Response results*/) {
        this.context = context;
//        this.results = results;
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
//        if(venue.getHours().getIsOpen()){
//            holder.suggestionHours.setText(context.getResources().getString(R.string.open_now));
//        } else {
//            holder.suggestionHours.setText(context.getResources().getString(R.string.closed));
//        }
//
//        double ratingColor = Double.parseDouble(venue.getRatingColor());
//        if (ratingColor >= 9.0) {
//            holder.suggestionRating.setTextColor(ContextCompat.getColor(context, R.color.Kale));
//        } else if (ratingColor >= 8.0) {
//            holder.suggestionRating.setTextColor(ContextCompat.getColor(context, R.color.Guacamole));
//        } else if (ratingColor >= 7.0) {
//            holder.suggestionRating.setTextColor(ContextCompat.getColor(context, R.color.Lime));
//        } else if (ratingColor >= 6.0) {
//            holder.suggestionRating.setTextColor(ContextCompat.getColor(context, R.color.Banana));
//        } else if (ratingColor >= 5.0) {
//            holder.suggestionRating.setTextColor(ContextCompat.getColor(context, R.color.Orange));
//        } else if (ratingColor >= 4.0) {
//            holder.suggestionRating.setTextColor(ContextCompat.getColor(context, R.color.MacCheese));
//        } else {
//            holder.suggestionRating.setTextColor(ContextCompat.getColor(context, R.color.Strawberry));
//        }

        if(mLastAnimatedItemPosition < position){
            animateItem(holder.itemView);
            mLastAnimatedItemPosition = position;
        }

        if(mItemsOnClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemsOnClickListener.onClick(venue);
                }
            });
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

        TextView suggestionName/*, suggestionHours, suggestionRating*/;

        ViewHolder(View itemView) {
            super(itemView);
            suggestionName = itemView.findViewById(R.id.suggestion_name);
//            suggestionHours = itemView.findViewById(R.id.suggestion_hours);
//            suggestionRating = itemView.findViewById(R.id.suggestion_rating);
        }
    }
}
