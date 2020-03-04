package com.example.mvc.codebase.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.support.v4.app.ActivityCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mvc.codebase.MyApplication;
import com.example.mvc.codebase.R;
import com.example.mvc.codebase.models.CountryModel;
import com.example.mvc.codebase.utils.Constants;
import com.example.mvc.codebase.utils.GenericView;
import com.example.mvc.codebase.utils.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

/**
 * This is adapter class manage the list of locations
 * e.g countryList, stateList, cityList
 */

public class AdpLocation extends BaseAdapter {

    private Context context;
    private ArrayList<CountryModel> listCountry;
    private String filter = "", strLoading = "";

    public AdpLocation(Context context, ArrayList<CountryModel> listCountry, String filter) {
        this.context = context;
        this.listCountry = listCountry;
        this.filter = filter;
        this.strLoading = Util.getAppKeyValue(context, R.string.lblLoading);
    }

    @Override
    public int getCount() {
        return listCountry.size();
    }

    @Override
    public Object getItem(int position) {
        return listCountry.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        try {

            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.list_item_location, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.footerView.setVisibility(View.GONE);

            String itemName;
            CountryModel countryCityModel = listCountry.get(position);
            holder.txtLocationName.setTag(countryCityModel);
            countryCityModel.setPosition(position);
            itemName = countryCityModel.getCountryName();

//			Change color on search
            int startPos = itemName.toLowerCase(Locale.US).indexOf(filter.toLowerCase(Locale.US));
            int endPos = startPos + filter.length();
            if (startPos != -1) // This should always be true, just a sanity check
            {
                ColorStateList searchedTextColour;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    searchedTextColour = new ColorStateList(new int[][]{new int[]{}}, new int[]{ActivityCompat.getColor(context, R.color.colorAccent)});
                } else {
                    searchedTextColour = new ColorStateList(new int[][]{new int[]{}}, new int[]{ActivityCompat.getColor(context, R.color.colorAccent)});
                }
                TextAppearanceSpan highlightSpan = new TextAppearanceSpan(null, Typeface.BOLD, -1, searchedTextColour, null);

                Spannable spannable = new SpannableString(itemName);
                spannable.setSpan(highlightSpan, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.txtLocationName.setText(spannable);
            } else
                holder.txtLocationName.setText(itemName);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    private class ViewHolder {
        private TextView txtLocationName, txtLoading;
        private View footerView;

        ViewHolder(View convertView) {

            txtLocationName = GenericView.findViewById(convertView, R.id.txtLocationName);
            txtLoading = GenericView.findViewById(convertView, R.id.txtLoading);
            footerView = GenericView.findViewById(convertView, R.id.footerview);

            //  Set Font Type
            txtLocationName.setTypeface(MyApplication.mTypefaceMap.get(Constants.HELVETICA_CONDENSED_MEDIUM));
            txtLoading.setTypeface(MyApplication.mTypefaceMap.get(Constants.HELVETICA_CONDENSED_MEDIUM));

        }
    }
}
