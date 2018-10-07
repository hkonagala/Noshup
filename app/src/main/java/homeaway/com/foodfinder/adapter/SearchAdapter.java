package homeaway.com.foodfinder.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import java.util.List;

import homeaway.com.foodfinder.R;
import homeaway.com.foodfinder.model.Restaurant;
import homeaway.com.foodfinder.model.VenueList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    // The application context for getting resources
    private Context context;

    // The list of restaurant results from the Foursquare API
    private List<Restaurant> venueList;

    public SearchAdapter(Context context, List<Restaurant> venueList) {
        this.context = context;
        this.venueList = venueList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        // Sets each view with the appropriate venue details
        Restaurant venue = venueList.get(position);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            holder.icon.setImageIcon(venue.getThumbnail());
        }
        holder.name.setText(venue.getRestaurantName());
        holder.category.setText(venue.getCategory());
        holder.distance.setText((venue.getLocation().getDistance()) + context.getString(R.string.miles));

        // Stores additional venue details for the map view
        holder.id = venue.getId();
        holder.latitude = venue.getLocation().getLat();
        holder.longitude = venue.getLocation().getLng();
    }

    @Override
    public int getItemCount() {
        return venueList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // The restaurant fields to display
        ImageView icon;
        TextView name;
        TextView category;
        TextView distance;
        LottieAnimationView animationView;
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
            //TODO
            animationView = itemView.findViewById(R.id.lottieAnimationView);

        }

        @Override
        public void onClick(View view) {
            //do something
        }
    }
}
