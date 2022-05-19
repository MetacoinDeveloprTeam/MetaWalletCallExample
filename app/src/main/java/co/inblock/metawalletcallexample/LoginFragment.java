package co.inblock.metawalletcallexample;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import co.inblock.metawalletcallexample.databinding.LoginFragmentBinding;

public class LoginFragment extends Fragment {
    private LoginFragmentBinding binding;

    ActivityResultLauncher<Intent>
            mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                int i = result.getResultCode();
                if (i == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    if (intent == null) {
                        return;
                    }
                    Bundle bundle = intent.getExtras();
                    String code = bundle.getString("code", "");

                    switch (code) {
                        case "0000":
                            binding.viResult.setText(R.string.success);
                            break;
                        case "9999":
                            binding.viResult.setText(R.string.cancel_by_user);
                            break;
                        default:
                            binding.viResult.setText(R.string.error);
                            break;
                    }
                    binding.viResultCode.setText(code);
                    binding.viResultMessage.setText(bundle.getString("message", ""));
                    binding.viResultAddress.setText(bundle.getString("data", ""));
                } else if (i == Activity.RESULT_CANCELED) {
                    binding.viResult.setText(R.string.cancel_by_user);
                    binding.viResultCode.setText("");
                    binding.viResultMessage.setText("");
                    binding.viResultAddress.setText("");
                }
            });

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        binding = LoginFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnLogin.setOnClickListener(v -> {
            // init
            Uri params = Uri.parse("metawallet://co.inblock");
            Intent intent = new Intent(Intent.ACTION_VIEW, params);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

            // set Action
            intent.putExtra("appAction", "login");

            // Common variables.
            intent.putExtra("appCallback", binding.viCallback.getValue());
            intent.putExtra("appReqKey", binding.viReqKey.getValue());
            intent.putExtra("appName", binding.viName.getValue());
            intent.putExtra("appIcon", binding.viIcons.getValue());
            if (binding.rdoMainnet.isChecked()) {
                intent.putExtra("network", "1");
            } else if (binding.rdoTestnet.isChecked()) {
                intent.putExtra("network", "5");
            }

            intent.putExtra("callbackType", "intent");
            if (binding.rdodeeplink.isChecked()) {
                intent.putExtra("callbackType", "deeplink");
            } else if (binding.rdoUrl.isChecked()) {
                intent.putExtra("callbackType", "url");
            }
            binding.viResult.setText("");
            binding.viResultCode.setText("");
            binding.viResultMessage.setText("");
            binding.viResultAddress.setText("");

            try {
                mStartForResult.launch(intent);
            } catch (Exception e) {
                binding.viResult.setText(R.string.metawallet_not_found);
                binding.viResultCode.setText("");
                binding.viResultMessage.setText(e.getLocalizedMessage());
                binding.viResultAddress.setText("");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}