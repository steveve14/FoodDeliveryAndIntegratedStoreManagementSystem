package com.fooddelivery.shop;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fooddelivery.shop.models.ApiResponse;
import com.fooddelivery.shop.models.CreateMenuRequest;
import com.fooddelivery.shop.models.MenuDto;
import com.fooddelivery.shop.network.ApiClient;
import com.fooddelivery.shop.network.ApiService;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductsFragment extends Fragment {

    private RecyclerView rvProducts;
    private TextView tvEmptyProducts;
    private ProductAdapter adapter;
    private String storeId;

    // 이미지 피커 상태 (다이얼로그 생명주기와 공유)
    private ActivityResultLauncher<String> pickImageLauncher;
    private ImageView pendingImageView;
    private String pendingImageBase64;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri == null) return;
                    String encoded = encodeImageToBase64(uri);
                    if (encoded != null) {
                        pendingImageBase64 = encoded;
                        if (pendingImageView != null) {
                            // 선택 직후 URI로 바로 표시 (빠름)
                            pendingImageView.setImageURI(uri);
                            pendingImageView.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Toast.makeText(getContext(), "이미지를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_products, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rvProducts = view.findViewById(R.id.rv_products);
        tvEmptyProducts = view.findViewById(R.id.tv_empty_products);
        Button btnAddMenu = view.findViewById(R.id.btn_add_menu);

        rvProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ProductAdapter(new ProductAdapter.OnProductActionListener() {
            @Override
            public void onEdit(MenuDto menu) {
                showMenuDialog(menu);
            }

            @Override
            public void onDelete(MenuDto menu) {
                confirmDelete(menu);
            }
        });
        rvProducts.setAdapter(adapter);

        btnAddMenu.setOnClickListener(v -> showMenuDialog(null));

        if (StoreManager.getInstance().getStoreId() != null) {
            storeId = StoreManager.getInstance().getStoreId();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (StoreManager.getInstance().getStoreId() != null) {
            storeId = StoreManager.getInstance().getStoreId();
        }
        loadMenus();
    }

    private void loadMenus() {
        if (storeId == null) {
            tvEmptyProducts.setText("먼저 설정에서 로그인해주세요.");
            tvEmptyProducts.setVisibility(View.VISIBLE);
            rvProducts.setVisibility(View.GONE);
            return;
        }

        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.getMenus(storeId).enqueue(new Callback<ApiResponse<List<MenuDto>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<MenuDto>>> call, @NonNull Response<ApiResponse<List<MenuDto>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<MenuDto> menus = response.body().getData();
                    if (menus != null && !menus.isEmpty()) {
                        adapter.setMenus(menus);
                        rvProducts.setVisibility(View.VISIBLE);
                        tvEmptyProducts.setVisibility(View.GONE);
                    } else {
                        rvProducts.setVisibility(View.GONE);
                        tvEmptyProducts.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<MenuDto>>> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "메뉴 목록 로드 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void showMenuDialog(@Nullable MenuDto existing) {
        boolean isNew = (existing == null);

        // 다이얼로그별 이미지 상태 초기화
        pendingImageBase64 = isNew ? null : existing.getImageUrl();

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(isNew ? "새 메뉴 등록" : "메뉴 수정");

        android.widget.LinearLayout layout = new android.widget.LinearLayout(getContext());
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(48, 32, 48, 16);

        EditText etName = new EditText(getContext());
        etName.setHint("메뉴 이름 *");
        if (!isNew) etName.setText(existing.getName());
        layout.addView(etName);

        EditText etPrice = new EditText(getContext());
        etPrice.setHint("가격 *");
        etPrice.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        if (!isNew) etPrice.setText(String.valueOf(existing.getPrice()));
        layout.addView(etPrice);

        EditText etDesc = new EditText(getContext());
        etDesc.setHint("설명 (선택)");
        if (!isNew && existing.getDescription() != null) etDesc.setText(existing.getDescription());
        layout.addView(etDesc);

        // 이미지 섹션
        TextView tvImageLabel = new TextView(getContext());
        tvImageLabel.setText(isNew ? "메뉴 이미지 * (필수)" : "메뉴 이미지");
        tvImageLabel.setPadding(0, 24, 0, 8);
        layout.addView(tvImageLabel);

        ImageView ivPreview = new ImageView(getContext());
        ivPreview.setAdjustViewBounds(true);
        ivPreview.setMaxHeight(500);
        ivPreview.setVisibility(View.GONE);
        layout.addView(ivPreview);

        // 기존 이미지가 있으면 Base64 디코딩해서 미리보기 표시
        if (pendingImageBase64 != null) {
            try {
                String base64Data = pendingImageBase64.contains(",")
                        ? pendingImageBase64.substring(pendingImageBase64.indexOf(",") + 1)
                        : pendingImageBase64;
                byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
                Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                if (bm != null) {
                    ivPreview.setImageBitmap(bm);
                    ivPreview.setVisibility(View.VISIBLE);
                }
            } catch (Exception ignored) {
                ivPreview.setVisibility(View.GONE);
            }
        }

        pendingImageView = ivPreview; // 피커 콜백이 이 뷰를 갱신

        Button btnPickImage = new Button(getContext());
        btnPickImage.setText(pendingImageBase64 != null ? "이미지 변경" : "이미지 선택");
        btnPickImage.setOnClickListener(v -> {
            btnPickImage.setText("이미지 변경");
            pickImageLauncher.launch("image/*");
        });
        layout.addView(btnPickImage);

        builder.setView(layout);
        // setPositiveButton에 null을 주어 자동 닫힘을 막고 아래서 직접 오버라이드
        builder.setPositiveButton(isNew ? "등록" : "수정", null);
        builder.setNegativeButton("취소", (d, w) -> pendingImageView = null);

        AlertDialog dialog = builder.create();
        dialog.show();

        // 유효성 검사 통과 시에만 닫히도록 버튼 오버라이드
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();

            if (name.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(getContext(), "이름과 가격을 입력하세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isNew && pendingImageBase64 == null) {
                Toast.makeText(getContext(), "메뉴 이미지를 선택해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            int price;
            try {
                price = Integer.parseInt(priceStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "가격을 확인해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }

            String imageUrl = pendingImageBase64;
            CreateMenuRequest request = new CreateMenuRequest(name, desc, price, true, imageUrl);
            ApiService api = ApiClient.getClient().create(ApiService.class);

            pendingImageView = null;
            dialog.dismiss();

            if (existing != null) {
                api.updateMenu(storeId, existing.getId(), request).enqueue(new Callback<ApiResponse<MenuDto>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<MenuDto>> call, @NonNull Response<ApiResponse<MenuDto>> response) {
                        Toast.makeText(getContext(), "수정 완료", Toast.LENGTH_SHORT).show();
                        loadMenus();
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<MenuDto>> call, @NonNull Throwable t) {
                        Toast.makeText(getContext(), "수정 실패", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                api.createMenu(storeId, request).enqueue(new Callback<ApiResponse<MenuDto>>() {
                    @Override
                    public void onResponse(@NonNull Call<ApiResponse<MenuDto>> call, @NonNull Response<ApiResponse<MenuDto>> response) {
                        Toast.makeText(getContext(), "등록 완료", Toast.LENGTH_SHORT).show();
                        loadMenus();
                    }

                    @Override
                    public void onFailure(@NonNull Call<ApiResponse<MenuDto>> call, @NonNull Throwable t) {
                        Toast.makeText(getContext(), "등록 실패", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void confirmDelete(MenuDto menu) {
        new AlertDialog.Builder(requireContext())
                .setTitle("메뉴 삭제")
                .setMessage(menu.getName() + "을(를) 삭제하시겠습니까?")
                .setPositiveButton("삭제", (dialog, which) -> {
                    ApiService api = ApiClient.getClient().create(ApiService.class);
                    api.deleteMenu(storeId, menu.getId()).enqueue(new Callback<ApiResponse<Void>>() {
                        @Override
                        public void onResponse(@NonNull Call<ApiResponse<Void>> call, @NonNull Response<ApiResponse<Void>> response) {
                            Toast.makeText(getContext(), "삭제 완료", Toast.LENGTH_SHORT).show();
                            loadMenus();
                        }

                        @Override
                        public void onFailure(@NonNull Call<ApiResponse<Void>> call, @NonNull Throwable t) {
                            Toast.makeText(getContext(), "삭제 실패", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("취소", null)
                .show();
    }

    /** 갤러리에서 선택한 이미지를 Base64 data URI로 인코딩 (JPEG 80%, 최대 800px) */
    private String encodeImageToBase64(Uri uri) {
        try (InputStream is = requireContext().getContentResolver().openInputStream(uri)) {
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            bitmap = scaleBitmapIfNeeded(bitmap, 800);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            byte[] bytes = baos.toByteArray();
            return "data:image/jpeg;base64," + Base64.encodeToString(bytes, Base64.NO_WRAP);
        } catch (Exception e) {
            return null;
        }
    }

    private Bitmap scaleBitmapIfNeeded(Bitmap bm, int maxSize) {
        int w = bm.getWidth(), h = bm.getHeight();
        if (w <= maxSize && h <= maxSize) return bm;
        float scale = Math.min((float) maxSize / w, (float) maxSize / h);
        return Bitmap.createScaledBitmap(bm, (int) (w * scale), (int) (h * scale), true);
    }
}