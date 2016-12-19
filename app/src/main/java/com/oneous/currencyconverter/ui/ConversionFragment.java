package com.oneous.currencyconverter.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.oneous.currencyconverter.App;
import com.oneous.currencyconverter.R;
import com.oneous.currencyconverter.presentation.ConversionPresenter;
import com.oneous.currencyconverter.presentation.ConversionView;

import java.text.DecimalFormat;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ConversionFragment extends Fragment implements ConversionView {
    @Inject
    ConversionPresenter presenter;

    @BindView(R.id.text_view)
    TextView textView;
    @BindView(R.id.conversion_rate_display)
    TextView conversionRateDisplay;
    @BindView(R.id.amount)
    EditText amountView;
    @BindView(R.id.loading)
    View loadingView;

    private Unbinder unbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getAppComponent(getActivity()).inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_conversion, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        presenter.setView(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        presenter.setView(null);
    }

    @Override
    public void display(double totalAmount, double conversionRate) {
        textView.setText("Amount in SGD : " + new DecimalFormat("#.##").format(totalAmount));
        conversionRateDisplay.setText("Conversion Rate : " +  new DecimalFormat("#.##").format(conversionRate));
    }

    @Override
    public void showLoading() {
        loadingView.setVisibility(View.VISIBLE);
        textView.setVisibility(View.GONE);
    }

    @Override
    public void hideLoading() {
        loadingView.setVisibility(View.GONE);
        textView.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.convert)
    public void convertAction() {
        double amount = Double.parseDouble(amountView.getText().toString());
        presenter.convert(amount);
    }

    @OnClick(R.id.clear_memory_and_disk_cache)
    public void clearMemoryAndDiskCache() {
        presenter.clearMemoryAndDiskCache();
    }
}
