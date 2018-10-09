package homeaway.com.foodfinder.adapter;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.squareup.picasso.Picasso;

import java.util.List;

import homeaway.com.foodfinder.R;
import homeaway.com.foodfinder.activity.DetailsActivity;
import homeaway.com.foodfinder.model.venueModel.Venue;
import homeaway.com.foodfinder.model.venueModel.Response;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    // The application context for getting resources
    private Context context;

    // The list of results from the Foursquare API
    private Response results;

    public SearchAdapter(Context context, Response results) {
        this.context = context;
        this.results = results;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        // Sets each view with the appropriate venue details
        Venue venue = results.getVenues().get(position);
        //load image with picasso
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Picasso.with(context).load(String.valueOf(venue.getCategories().get(0).getIcon()))
                    .error(context.getDrawable(R.drawable.ic_error_black_24dp))
                    .into(holder.icon);
        }
        holder.name.setText(venue.getName());
        holder.category.setText(venue.getCategories().get(0).getShortName());
        holder.distance.setText((venue.getLocation().getDistance()) + context.getString(R.string.miles));

        //additional venue details for the map
        holder.id = venue.getId();
        holder.latitude = venue.getLocation().getLat();
        holder.longitude = venue.getLocation().getLng();
    }

    @Override
    public int getItemCount() {
        List<Venue> venues = results.getVenues();
        if (venues != null) {
            return venues.size();
        } else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // The venue fields to display
        ImageView icon;
        TextView name;
        TextView category;
        TextView distance;
        LottieAnimationView animationView;

        //details for map
        String id;
        double latitude;
        double longitude;

        ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            // Gets the appropriate view for each venue detail
            icon = itemView.findViewById(R.id.search_icon);
            name = itemView.findViewById(R.id.search_name);
            category = itemView.findViewById(R.id.search_category);
            distance = itemView.findViewById(R.id.search_distance);
            animationView = itemView.findViewById(R.id.lottieAnimationView);
            animationView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            int id = view.getId();

            if(id == R.id.search_item_container) {
                //intent to redirect the user to a details page with map
                Context context = name.getContext();
                Intent i = new Intent(context, DetailsActivity.class);

                // Passes the extra venue details onto the map
                i.putExtra("name", name.getText());
                i.putExtra("ID", id);
                i.putExtra("latitude", latitude);
                i.putExtra("longitude", longitude);
                context.startActivity(i);
            } else if(id == R.id.lottieAnimationView) {
                startBookmarkAnimation();
            }

        }

        private void startBookmarkAnimation() {
            ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f).setDuration(500);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    animationView.setProgress((Float) valueAnimator.getAnimatedValue());
                }
            });

            if (animationView.getProgress() == 0f) {
                animator.start();
            } else {
                animationView.setProgress(0f);
            }
        }
    }
}
