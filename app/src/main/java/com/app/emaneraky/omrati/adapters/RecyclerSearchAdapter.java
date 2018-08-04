package com.app.emaneraky.omrati.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.emaneraky.omrati.R;
import com.app.emaneraky.omrati.models.Company;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerSearchAdapter  extends RecyclerView.Adapter<RecyclerSearchAdapter.MyViewHolder>
        implements Filterable {
    private Context context;
    private List<Company> searchList;
    private List<Company> searchListFiltered;
    private CompanyAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, detail;
        public CircleImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            name =(TextView) view.findViewById(R.id.city_displayname);
            detail = (TextView)view.findViewById(R.id.detail_city);
            thumbnail =(CircleImageView) view.findViewById(R.id.city_img);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    int position = (int) view.getTag();
                    listener.onCompanySelected(searchListFiltered.get(getAdapterPosition()));
                    // send selected contact in callback
//                    listener.onContactSelected(contactListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }


    public RecyclerSearchAdapter(Context context, List<Company> searchList, CompanyAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.searchList = searchList;
        this.searchListFiltered = searchList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Company contact = searchListFiltered.get(position);
        holder.name.setText(contact.getFirstName());
        holder.detail.setText(contact.getEmail());

        Picasso.with(context).load(contact.getProfileImage()).into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return searchListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    searchListFiltered = searchList;
                } else {
                    List<Company> filteredList = new ArrayList<>();
                    for (Company row : searchList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getFirstName().toLowerCase().contains(charString.toLowerCase()) || row.getEmail().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    searchListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = searchListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                searchListFiltered = (ArrayList<Company>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface CompanyAdapterListener {
        void onCompanySelected(Company company);
    }
}
