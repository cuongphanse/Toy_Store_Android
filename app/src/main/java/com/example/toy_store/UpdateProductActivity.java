package com.example.toy_store;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.toy_store.model.Product;
import com.example.toy_store.repository.ProductRepository;
import com.example.toy_store.service.ProductService;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProductActivity extends AppCompatActivity {

    private EditText etName, etDescription, etPrice, etStockQuantity;
    private ImageView imgProduct;
    private Button btnUpdate, btnCancel, btnChangeImage;
    private Product product;
    private Uri imageUri; // To hold the image URI

    // Result Launcher for selecting images
    private ActivityResultLauncher<String> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                imageUri = uri;
                imgProduct.setImageURI(imageUri);
            }
    );

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_product);

        etName = findViewById(R.id.et_nameProduct);
        etDescription = findViewById(R.id.et_descriptionProduct);
        etPrice = findViewById(R.id.et_priceProduct);
        etStockQuantity = findViewById(R.id.et_stockQuantity);
        imgProduct = findViewById(R.id.img_product);
        btnUpdate = findViewById(R.id.btn_update_product);
        btnCancel = findViewById(R.id.btn_cancel);
        btnChangeImage = findViewById(R.id.btn_change_image);

        product = (Product) getIntent().getSerializableExtra("product");

        if (product != null) {
            etName.setText(product.getName());
            etDescription.setText(product.getDescription());
            etPrice.setText(String.valueOf(product.getPrice()));
            etStockQuantity.setText(String.valueOf(product.getStockQuantity()));
            Glide.with(this).load(product.getUrlImage()).into(imgProduct);
        }

        btnChangeImage.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        btnUpdate.setOnClickListener(v -> updateProduct());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void updateProduct() {
        String name = etName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        double price;
        int stockQuantity;

        try {
            price = Double.parseDouble(etPrice.getText().toString().trim());
            stockQuantity = Integer.parseInt(etStockQuantity.getText().toString().trim());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid price and stock quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        if (name.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Name and description cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody namePart = RequestBody.create(name, MediaType.parse("text/plain"));
        RequestBody descriptionPart = RequestBody.create(description, MediaType.parse("text/plain"));
        RequestBody pricePart = RequestBody.create(String.valueOf(price), MediaType.parse("text/plain"));
        RequestBody stockQuantityPart = RequestBody.create(String.valueOf(stockQuantity), MediaType.parse("text/plain"));

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.addInterceptor(logging);

        ProductService productService = ProductRepository.getUserService(httpClient.build());

        Call<Void> call;
        if (imageUri != null) {
            MultipartBody.Part imagePart = prepareImageFilePart("urlImage", imageUri);
            call = productService.updateProduct(product.getId(), namePart, descriptionPart, pricePart, stockQuantityPart, imagePart);
        } else {
            call = productService.updateWithoutImage(product.getId(), namePart, descriptionPart, pricePart, stockQuantityPart);
        }

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(UpdateProductActivity.this, "Product updated successfully", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK); // Set result code to indicate success
                    finish();
                } else {
                    try {
                        String errorMessage = response.errorBody().string();
                        Toast.makeText(UpdateProductActivity.this, "Update failed: " + errorMessage, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(UpdateProductActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(UpdateProductActivity.this, "Update failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private MultipartBody.Part prepareImageFilePart(String partName, Uri fileUri) {
        File file = FileUtils.getFileFromUri(getContentResolver(), fileUri, this);
        RequestBody requestFile = RequestBody.create(file, MediaType.parse(getContentResolver().getType(fileUri)));
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }
}
