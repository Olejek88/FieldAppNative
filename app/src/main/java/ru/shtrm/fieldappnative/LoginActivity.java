package ru.shtrm.fieldappnative;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.shtrm.fieldappnative.db.realm.User;
import ru.shtrm.fieldappnative.rest.AppAPIFactory;
import ru.shtrm.fieldappnative.serverapi.TokenSrv;
import ru.shtrm.fieldappnative.utils.MainUtil;

public class LoginActivity extends AppCompatActivity {

    private EditText userSelect;
    private EditText pinCode;
    private TextView loginError;
    private Realm realmDB;
    private boolean isLogged = false;
    private ProgressDialog authorizationDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        realmDB = Realm.getDefaultInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(
                    ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }

        initViews();
    }

    public void initViews() {
        userSelect = findViewById(R.id.user_select);
        pinCode = findViewById(R.id.login_pin);
        loginError = findViewById(R.id.login_error);
        loginError.setBackgroundColor(getResources().getColor(R.color.md_red_800));

        pinCode.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Button b = findViewById(R.id.loginButton);
                    b.performClick();
                    return true;
                }
                return false;
            }
        });
        pinCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loginError.setVisibility(View.GONE);
                if (s.length() == 4) {
                    Button b = findViewById(R.id.loginButton);
                    b.performClick();
                }
            }

        });
        pinCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
        pinCode.requestFocus();

        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onClickLogin(v);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_BACK || super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        Log.d("xxxx", "LoginActivity:onPause()");
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.d("xxxx", "LoginActivity:onResume()");
        super.onResume();
    }

    /**
     * Обработчик клика кнопки "Войти"
     *
     * @param view Event's view
     */
    public void onClickLogin(View view) {
        setEnabledLoginButton(false);
        final String userName = userSelect.getText().toString();
        String enteredPin = pinCode.getText().toString();
        String enteredPinMD5 = MainUtil.MD5(enteredPin);

        if (enteredPinMD5 != null) {
            // проверяем, есть соединение с инетом или нет
            // если нет, искать по метке в локальной базе, к сети вообще не обращаться.
            if (!FieldApplication.isInternetOn(getApplicationContext())) {
                Toast.makeText(getApplicationContext(), getText(R.string.no_internet), Toast.LENGTH_LONG).show();
                // проверяем наличие пользователя в локальной базе
                User user = realmDB.where(User.class)
                        .equalTo("login", userName)
                        .and()
                        .equalTo("pass", enteredPinMD5)
                        .findFirst();
                // в зависимости от результата либо дать работать, либо не дать
                if (user != null && user.isActive() == 1) {
                    isLogged = true;
                    //changeActiveProfile(user);
                    AuthorizedUser.getInstance().setUuid(user.getUuid());
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.toast_error_no_access),
                            Toast.LENGTH_LONG).show();
                }
                realmDB.close();
                setResult(RESULT_CANCELED);
                finish();
            }

            // показываем диалог входа
            authorizationDialog = new ProgressDialog(LoginActivity.this);
            authorizationDialog.setMessage(getString(R.string.toast_enter));
            authorizationDialog.setIndeterminate(true);
            authorizationDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            authorizationDialog.setCancelable(false);
            authorizationDialog.show();

            // запрашиваем токен
            Call<TokenSrv> call = AppAPIFactory.getTokenService().getByLabel(userName, TokenSrv.Type.LABEL);
            call.enqueue(new Callback<TokenSrv>() {
                @Override
                public void onResponse(Call<TokenSrv> tokenSrvCall, Response<TokenSrv> response) {
                    if (response.code() != 200) {
                        String message = response.message() != null && !response.message().isEmpty() ? response.message() : getString(R.string.auth_error);
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }

                    AuthorizedUser authUser = AuthorizedUser.getInstance();
                    TokenSrv token = response.body();
                    if (token != null) {
                        authUser.setToken(token.getAccessToken());
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.toast_token_received), Toast.LENGTH_SHORT).show();
                        // Сохраняем login в AuthorizedUser для дальнейших запросв статики
                        authUser.setLogin(token.getUserName());
                    } else {
                        RealmResults<User> user = realmDB.where(User.class).equalTo("tagId",
                                authUser.getTagId()).findAll();
                        if (user.size() == 1) {
                            authUser.setLogin(user.first().getLogin());
                        }
                        realmDB.close();
                    }

                    // запрашиваем актуальную информацию по пользователю
                    Call<User> call = AppAPIFactory.getUserService().user();
                    call.enqueue(new Callback<User>() {
                        @Override
                        public void onResponse(Call<User> userCall, Response<User> response) {
                            if (response.code() != 200) {
                                String message = response.message() != null && !response.message().isEmpty() ? response.message() : getString(R.string.toast_error_no_user);
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                            }
                            User user = response.body();
                            if (user != null) {
                                final String fileName = user.getImage();
                                Realm realm = Realm.getDefaultInstance();
                                // проверяем необходимость запрашивать файл изображения с сервера
                                String tagId = AuthorizedUser.getInstance().getTagId();
                                User localUser = realm.where(User.class).equalTo("tagId", tagId).findFirst();
                                File localImageFile;
                                boolean needDownloadImage = false;
                                if (localUser != null) {
                                    Date serverDate = user.getChangedAt();
                                    Date localDate = localUser.getChangedAt();
                                    File fileDir = getExternalFilesDir(localUser.getImageFilePath() + "/");
                                    localImageFile = new File(fileDir, localUser.getImage());
                                    if (localDate.getTime() < serverDate.getTime() || !localImageFile.exists()) {
                                        needDownloadImage = true;
                                    }
                                } else {
                                    needDownloadImage = true;
                                }

                                realm.beginTransaction();
                                realm.copyToRealmOrUpdate(user);
                                realm.commitTransaction();
                                isLogged = true;
                                // TODO: нужен метод для установки полей объекта из разных объектов (Token, User...)
                                AuthorizedUser authorizedUser = AuthorizedUser.getInstance();
                                authorizedUser.setUuid(user.getUuid());
                                authorizedUser.setLogin(user.getLogin());
                                realm.close();
                                setResult(RESULT_OK);
                                finish();
                            } else {
                                String message = getString(R.string.toast_error_no_user);
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                            }

                            authorizationDialog.dismiss();
                        }

                        @Override
                        public void onFailure(Call<User> userCall, Throwable t) {
                            // сообщаем описание неудачи
                            // TODO нужен какой-то механизм уведомления о причине не успеха
//                                    String message = bundle.getString(IServiceProvider.MESSAGE);
                            String message = getString(R.string.toast_error_no_user_received);
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                            authorizationDialog.dismiss();
                        }
                    });
                }

                @Override
                public void onFailure(Call<TokenSrv> tokenSrvCall, Throwable t) {
                    // TODO нужен какой-то механизм уведомления о причине не успеха
                    String message = getString(R.string.toast_error_no_token_received);
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    // TODO реализовать проверку на то что пользователя нет на сервере
                    // токен не получен, сервер не ответил...
                    // проверяем наличие пользователя в локальной базе
                    User user = realmDB.where(User.class)
                            .equalTo("tagId", AuthorizedUser.getInstance().getTagId())
                            .findFirst();
                    // в зависимости от результата либо дать работать, либо не дать
                    if (user != null && user.isActive() == 1) {
                        isLogged = true;
                        AuthorizedUser.getInstance().setUuid(user.getUuid());
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.toast_error_no_access),
                                Toast.LENGTH_LONG).show();
                    }
                    authorizationDialog.dismiss();
                }
            });
            // завершаем окно входа
            setResult(RESULT_OK);
        } else {
            loginError.setVisibility(View.VISIBLE);
        }
        setEnabledLoginButton(true);
    }

    /**
     * Включает / отключает кнопку "Войти" на экране входа.
     *
     * @param enable Режим.
     */
    private void setEnabledLoginButton(boolean enable) {
        Button loginButton = findViewById(R.id.loginButton);
        if (loginButton != null) {
            loginButton.setEnabled(enable);
            loginButton.setClickable(enable);
        }
    }
}