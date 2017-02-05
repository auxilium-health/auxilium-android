package com.pluscubed.auxilium.choose;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pluscubed.auxilium.R;
import com.pluscubed.auxilium.business.drugbank.Ingredient;
import com.pluscubed.auxilium.business.drugbank.Product;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

class ChooseAdapter extends RecyclerView.Adapter<ChooseAdapter.ProductHolder> {

    private final Context context;
    ChooseController controller;
    List<Product> products;

    public ChooseAdapter(Context context, ChooseController controller) {
        this.context = context;
        products = new ArrayList<>();
        this.controller = controller;
    }

    @Override
    public long getItemId(int position) {
        return products.get(position).hashCode();
    }

    @Override
    public ProductHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_auto_complete, parent, false);
        return new ProductHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductHolder holder, int position) {
        holder.bind(products.get(position));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void setItems(List<Product> items) {
        this.products = items;
        notifyDataSetChanged();
    }

    public class ProductHolder extends RecyclerView.ViewHolder {

        private final View itemView;
        @BindView(R.id.image)
        ImageView image;
        @BindView(R.id.text1)
        TextView text;
        @BindView(R.id.text2)
        TextView text2;

        public ProductHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            ButterKnife.bind(this, this.itemView);
        }

        public void bind(Product product) {
            if (product.getHits().size() > 0) {
                text.setText(Html.fromHtml(product.getHits().get(0).getValue()));
            } else {
                text.setText(product.getName());
            }

            text2.setText("NDC Codes: " + TextUtils.join(" • ", product.getNdcProductCodes()) + "\n");
            text2.append("Ingredients: ");
            List<Ingredient> ingredients = product.getIngredients();
            for (int i = 0; i < ingredients.size(); i++) {
                Ingredient ingredient = ingredients.get(i);
                text2.append(ingredient.getName() + " ");
                if (ingredient.getStrengthNumber() != null) {
                    text2.append(ingredient.getStrengthNumber() + ingredient.getStrengthUnit());
                }
                if (i != ingredients.size() - 1) {
                    text2.append(" • ");
                }
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    controller.onClick(products.get(getAdapterPosition()));
                }
            });

        }


    }
}
