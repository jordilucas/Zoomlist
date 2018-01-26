package br.com.calculafeira.calculafeira.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.calculafeira.calculafeira.Adapter.AdapterSpinnerCategory;
import br.com.calculafeira.calculafeira.Model.Product;
import br.com.calculafeira.calculafeira.Model.ProductData;
import br.com.calculafeira.calculafeira.Persistence.DataManager;
import br.com.calculafeira.calculafeira.R;
import br.com.calculafeira.calculafeira.Util.Helpers;
import br.com.calculafeira.calculafeira.Util.ItemData;

public class ProductCreateEdit extends AppCompatActivity {

    EditText name_product, price_product;
    Spinner category_product;
    ProductData productData;
    Product product;
    private String current = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_create);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        product = new Product();
        productData = new ProductData();

        name_product = (EditText)findViewById(R.id.edit_text_name_product);
        price_product = (EditText)findViewById(R.id.edit_text_price_product);
        category_product = (Spinner)findViewById(R.id.spinner_category_product);

        String[] categories = getResources().getStringArray(R.array.categories);
        Integer[] images = { 0, R.drawable.ico_alimento, R.drawable.ico_bebida,
                R.drawable.ico_limpeza, R.drawable.ico_higiene };

        ArrayList<ItemData> list = new ArrayList<>();
        for (int i = 0; i < list.size();i++) {
            list.add(new ItemData(categories[i],images[i]));
        }

        AdapterSpinnerCategory adapter = new AdapterSpinnerCategory(
                this,
                R.layout.adapter_spinner_category,
                R.id.textView_spinner_category,
                list
        );
        category_product.setAdapter(adapter);

        setTitle(R.string.cadastrar_produto);
        if (getIntent().hasExtra("productData")) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                productData = (ProductData) extras.getSerializable("productData");
                if (productData != null) {
                    setTitle(R.string.editar_produto);
                    name_product.setText(productData.getProduct().getNameProduct());
                    price_product.setText(String.valueOf(Helpers.getMonetary(String.valueOf(productData.getPrice()))));
                    List lista = Arrays.asList(getResources().getStringArray(R.array.categories));
                    int index = lista.indexOf(productData.getProduct().getNameCategory());
                    category_product.setSelection(index);
                    product.setIdProduct(productData.getProduct().getIdProduct());
                }
            }
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try
                {
                    product.setNameCategory(category_product.getSelectedItem().toString());
                    product.setNameProduct(name_product.getText().toString());
                    Long idProduct = DataManager.getInstance().getProductDAO().save(product);
                    productData.setFkProduct(idProduct);
                    productData.setPrice(Double.parseDouble(price_product.getText().toString().replaceAll("[R$,.]", "")));
                    productData.setQuantity(0);
                    Long idProductData = DataManager.getInstance().getProductDataDAO().save(productData);

                    Intent intent = new Intent(ProductCreateEdit.this, MainList.class);
                    startActivity(intent);

                    Snackbar.make(view, "Produto salvo com sucesso", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                catch (Exception ex)
                {
                    Snackbar.make(view, "ERRO:" + ex.getMessage(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    Log.e("Erro ProductCreateEdit", ex.getMessage());
                }
            }
        });

        price_product.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().equals(current)){
                    price_product.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[R$,.]", "");

                    double parsed = Double.parseDouble(cleanString);
                    String formatted = NumberFormat.getCurrencyInstance().format((parsed/100));

                    current = formatted;
                    price_product.setText(formatted);
                    price_product.setSelection(formatted.length());

                    price_product.addTextChangedListener(this);
                }
            }
        });
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}