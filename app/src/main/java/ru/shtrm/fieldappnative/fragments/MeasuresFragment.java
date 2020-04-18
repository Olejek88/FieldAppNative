package ru.shtrm.fieldappnative.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import ru.shtrm.fieldappnative.AuthorizedUser;
import ru.shtrm.fieldappnative.R;
import ru.shtrm.fieldappnative.ToirApplication;
import ru.shtrm.fieldappnative.db.SortField;
import ru.shtrm.fieldappnative.db.realm.Channel;
import ru.shtrm.fieldappnative.db.realm.IToirDbObject;
import ru.shtrm.fieldappnative.db.realm.MeasureType;
import ru.shtrm.fieldappnative.db.realm.MeasuredValue;
import ru.shtrm.fieldappnative.rest.AppAPIFactory;
import ru.shtrm.fieldappnative.rest.AppAPIResponse;

public class MeasuresFragment extends Fragment {
    private static final String TAG = "MeasuresFragment";
    private Realm realmDB;

    private ListView contentListView;

    public static MeasuresFragment newInstance() {
        return (new MeasuresFragment());
    }

    /**
     * Метод для обновления справочников необходимых для работы с нарядом.
     */
    public static void updateReferencesForOrders(final Context context) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final Date currentDate = new Date();
                String changedDate;
                String referenceName;
                Realm realm = Realm.getDefaultInstance();

                // OrderLevel
                referenceName = OrderLevel.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<OrderLevel>> response = AppAPIFactory.getOrderLevelService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<OrderLevel> list = response.body();
                        if (list.size() > 0) {
                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(list);
                            realm.commitTransaction();
                            ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // OrderStatus
                referenceName = OrderStatus.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<OrderStatus>> response = AppAPIFactory.getOrderStatusService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<OrderStatus> list = response.body();
                        if (list.size() > 0) {
                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(list);
                            realm.commitTransaction();
                            ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // OrderVerdict
                referenceName = OrderVerdict.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<OrderVerdict>> response = AppAPIFactory.getOrderVerdictService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<OrderVerdict> list = response.body();
                        if (list.size() > 0) {
                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(list);
                            realm.commitTransaction();
                            ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // TaskVerdict
                referenceName = TaskVerdict.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<TaskVerdict>> response = AppAPIFactory.getTaskVerdictService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<TaskVerdict> list = response.body();
                        if (list.size() > 0) {
                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(list);
                            realm.commitTransaction();
                            ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // TaskStatus
                referenceName = TaskStatus.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<TaskStatus>> response = AppAPIFactory.getTaskStatusService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<TaskStatus> list = response.body();
                        if (list.size() > 0) {
                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(list);
                            realm.commitTransaction();
                            ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // EquipmentStatus
                referenceName = EquipmentStatus.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<EquipmentStatus>> response = AppAPIFactory
                            .getEquipmentStatusService().get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<EquipmentStatus> list = response.body();
                        if (list.size() > 0) {
                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(list);
                            realm.commitTransaction();
                            ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // StageVerdict
                referenceName = StageVerdict.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<StageVerdict>> response = AppAPIFactory.getStageVerdictService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<StageVerdict> list = response.body();
                        if (list.size() > 0) {
                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(list);
                            realm.commitTransaction();
                            ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // StageStatus
                referenceName = StageStatus.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<StageStatus>> response = AppAPIFactory.getStageStatusService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<StageStatus> list = response.body();
                        if (list.size() > 0) {
                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(list);
                            realm.commitTransaction();
                            ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // OperationVerdict
                referenceName = OperationVerdict.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<OperationVerdict>> response = AppAPIFactory
                            .getOperationVerdictService().get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<OperationVerdict> list = response.body();
                        if (list.size() > 0) {
                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(list);
                            realm.commitTransaction();
                            ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // OperationStatus
                referenceName = OperationStatus.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<OperationStatus>> response = AppAPIFactory
                            .getOperationStatusService().get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<OperationStatus> list = response.body();
                        if (list.size() > 0) {
                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(list);
                            realm.commitTransaction();
                            ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // MeasureType
                referenceName = MeasureType.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<MeasureType>> response = AppAPIFactory.getMeasureTypeService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<MeasureType> list = response.body();
                        if (list.size() > 0) {
                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(list);
                            realm.commitTransaction();
                            ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // AttributeType
                referenceName = AttributeType.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<AttributeType>> response = AppAPIFactory.getAttributeTypeService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<AttributeType> list = response.body();
                        if (list.size() > 0) {
                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(list);
                            realm.commitTransaction();
                            ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // EquipmentAttribute
                referenceName = EquipmentAttribute.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<EquipmentAttribute>> response = AppAPIFactory.getEquipmentAttributeService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        // TODO: реализовать механизм проверки наличия изменённых данных локально
                        // при необходимости отбрасывать данные с сервера
                        List<EquipmentAttribute> list = response.body();
                        if (list.size() > 0) {
                            // сразу ставим флаг что они "отправлены", чтоб избежать их повторной отправки
                            for (EquipmentAttribute item : list) {
                                item.setSent(true);
                            }

                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(list);
                            realm.commitTransaction();
                            ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // DefectType
                referenceName = DefectType.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<DefectType>> response = AppAPIFactory.getDefectTypeService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<DefectType> list = response.body();
                        if (list.size() > 0) {
                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(list);
                            realm.commitTransaction();
                            ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // DefectLevel
                referenceName = DefectLevel.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<DefectLevel>> response = AppAPIFactory.getDefectLevelService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<DefectLevel> list = response.body();
                        if (list.size() > 0) {
                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(list);
                            realm.commitTransaction();
                            ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Documentation
                referenceName = Documentation.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<Documentation>> response = AppAPIFactory.getDocumentationService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<Documentation> list = response.body();
                        List<FilePath> files = new ArrayList<>();
                        File extDir = context.getExternalFilesDir("");
                        AuthorizedUser user = AuthorizedUser.getInstance();
                        String userName = user.getLogin();
                        if (extDir == null) {
                            throw new Exception("Unable get extDir!!!");
                        }

                        for (Documentation item : list) {
                            String localPath = item.getImageFilePath() + "/";
                            if (isNeedDownload(extDir, item, localPath, item.isRequired())) {
                                String url = item.getImageFileUrl(userName) + "/";
                                files.add(new FilePath(item.getPath(), url, localPath));
                            }
                        }

                        if (list.size() > 0) {
                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(list);
                            realm.commitTransaction();
                            ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                        }

                        Map<String, Set<String>> requestList = new HashMap<>();
                        // тестовый вывод для принятия решения о группировке файлов для минимизации количества загружаемых данных
                        for (FilePath item : files) {
                            String key = item.urlPath + item.fileName;
                            if (!requestList.containsKey(key)) {
                                Set<String> listOfDoc = new HashSet<>();
                                listOfDoc.add(item.localPath);
                                requestList.put(key, listOfDoc);
                            } else {
                                requestList.get(key).add(item.localPath);
                            }
                        }

                        // загружаем файлы
                        for (String key : requestList.keySet()) {
                            Call<ResponseBody> callFile = AppAPIFactory.getFileDownload().get(ToirApplication.serverUrl + key);
                            try {
                                retrofit2.Response<ResponseBody> r = callFile.execute();
                                ResponseBody trueImgBody = r.body();
                                if (trueImgBody == null) {
                                    continue;
                                }

                                for (String localPath : requestList.get(key)) {
                                    String fileName = key.substring(key.lastIndexOf("/") + 1);
                                    File file = new File(extDir.getAbsolutePath() + '/' + localPath, fileName);
                                    if (!file.getParentFile().exists()) {
                                        if (!file.getParentFile().mkdirs()) {
                                            Log.e(TAG, "Не удалось создать папку " +
                                                    file.getParentFile().toString() +
                                                    " для сохранения файла изображения!");
                                            continue;
                                        }
                                    }

                                    FileOutputStream fos = new FileOutputStream(file);
                                    fos.write(trueImgBody.bytes());
                                    fos.close();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // CommonFile
                referenceName = CommonFile.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<CommonFile>> response = AppAPIFactory.getCommonFileService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<CommonFile> list = response.body();
                        File extDir = context.getExternalFilesDir("");
                        AuthorizedUser user = AuthorizedUser.getInstance();
                        String userName = user.getLogin();
                        if (extDir == null) {
                            throw new Exception("Unable get extDir!!!");
                        }

                        for (CommonFile item : list) {
                            String localPath = CommonFile.getImageRoot();
                            item.setPath(localPath);
                        }

                        if (list.size() > 0) {
                            realm.beginTransaction();
                            realm.copyToRealmOrUpdate(list);
                            realm.commitTransaction();
                            ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                        }

                        // загружаем файлы
                        for (CommonFile item : list) {
                            if (!isNeedDownload(extDir, item, item.getPath(), item.isRequire())) {
                                continue;
                            }

                            String url = null;
                            Response<AppAPIResponse> urlResponse = AppAPIFactory.getCommonFileService()
                                    .getUrl(item.getUuid()).execute();
                            if (response.isSuccessful()) {
                                AppAPIResponse data = urlResponse.body();
                                url = (String) data.getData();
                                if (url == null || url.equals("")) {
                                    continue;
                                }

                                url = ToirApplication.serverUrl + data.getData();
                            }

                            Call<ResponseBody> callFile = AppAPIFactory.getFileDownload().get(url);
                            try {
                                retrofit2.Response<ResponseBody> r = callFile.execute();
                                ResponseBody trueImgBody = r.body();
                                if (trueImgBody == null) {
                                    continue;
                                }

                                File file = new File(extDir.getAbsolutePath() + '/' + item.getPath(), item.getName());
                                if (!file.getParentFile().exists()) {
                                    if (!file.getParentFile().mkdirs()) {
                                        Log.e(TAG, "Не удалось создать папку " +
                                                file.getParentFile().toString() +
                                                " для сохранения файла!");
                                        continue;
                                    }
                                }

                                FileOutputStream fos = new FileOutputStream(file);
                                fos.write(trueImgBody.bytes());
                                fos.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                realm.close();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    /**
     * Обновляет тупо все справочники без разбора необходимости.
     * Неиспользовать!
     *
     * @param dialog    Диалог показывающий процесс обновления справочников
     */
    public static void updateReferences(final ProgressDialog dialog) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // получаем справочники, обновляем всё несмотря на то что часть данных будет дублироваться
                final Date currentDate = new Date();
                String changedDate;
                String referenceName;
                Realm realm = Realm.getDefaultInstance();

                // AlertType
                referenceName = AlertType.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<AlertType>> response = AppAPIFactory.getAlertTypeService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<AlertType> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Contragent
                referenceName = Contragent.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<Contragent>> response = AppAPIFactory.getContragentService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<Contragent> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // CriticalType
                referenceName = CriticalType.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<CriticalType>> response = AppAPIFactory.getCriticalTypeService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<CriticalType> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // DefectType
                referenceName = DefectType.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<DefectType>> response = AppAPIFactory.getDefectTypeService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<DefectType> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Channel
                referenceName = Channel.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<Channel>> response = AppAPIFactory.getDefectService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<Channel> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Documentation
                // нужно ли вообще таким образом обновлять этот справочник???
                referenceName = Documentation.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<Documentation>> response = AppAPIFactory.getDocumentationService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<Documentation> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // DocumentationType ???
                referenceName = DocumentationType.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<DocumentationType>> response = AppAPIFactory
                            .getDocumentationTypeService().get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<DocumentationType> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Equipment ???
                referenceName = Equipment.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<Equipment>> response = AppAPIFactory.getEquipmentService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<Equipment> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // EquipmentModel ???
                referenceName = EquipmentModel.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<EquipmentModel>> response = AppAPIFactory
                            .getEquipmentModelService().get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<EquipmentModel> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // EquipmentStatus
                referenceName = EquipmentStatus.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<EquipmentStatus>> response = AppAPIFactory
                            .getEquipmentStatusService().get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<EquipmentStatus> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // EquipmentType ??
                referenceName = EquipmentType.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<EquipmentType>> response = AppAPIFactory.getEquipmentTypeService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<EquipmentType> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // MeasuredValue
                referenceName = MeasuredValue.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<MeasuredValue>> response = AppAPIFactory.getMeasuredValueService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<MeasuredValue> list = response.body();
                        // устанавливаем флаг того данные были уже отправлены на сервер
                        for (MeasuredValue value : list) {
                            value.setSent(true);
                        }

                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // MeasureType
                referenceName = MeasureType.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<MeasureType>> response = AppAPIFactory.getMeasureTypeService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<MeasureType> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Operation ???
                referenceName = Operation.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<Operation>> response = AppAPIFactory.getOperationService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<Operation> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // OperationStatus
                referenceName = OperationStatus.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<OperationStatus>> response = AppAPIFactory
                            .getOperationStatusService().get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<OperationStatus> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // OperationTemplate
                referenceName = OperationTemplate.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<OperationTemplate>> response = AppAPIFactory
                            .getOperationTemplateService().get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<OperationTemplate> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // OperationTool
                referenceName = OperationTool.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<OperationTool>> response = AppAPIFactory
                            .getOperationToolService().get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<OperationTool> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // OperationType
                referenceName = OperationType.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<OperationType>> response = AppAPIFactory
                            .getOperationTypeService().get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<OperationType> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // ObjectType
                referenceName = ObjectType.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<ObjectType>> response = AppAPIFactory
                            .getObjectTypeService().get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<ObjectType> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Objects
                referenceName = Objects.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<Objects>> response = AppAPIFactory.getObjectService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<Objects> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // OperationVerdict
                referenceName = OperationVerdict.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<OperationVerdict>> response = AppAPIFactory
                            .getOperationVerdictService().get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<OperationVerdict> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // OrderLevel
                referenceName = OrderLevel.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<OrderLevel>> response = AppAPIFactory.getOrderLevelService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<OrderLevel> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Orders ???
                referenceName = Orders.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<Orders>> response = AppAPIFactory.getOrdersService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<Orders> list = response.body();
                        // устанавливаем флаг того данные были уже отправлены на сервер
                        for (Orders order : list) {
                            order.setSent(true);
                        }

                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // OrderStatus
                referenceName = OrderStatus.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<OrderStatus>> response = AppAPIFactory.getOrderStatusService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<OrderStatus> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // OrderVerdict
                referenceName = OrderVerdict.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<OrderVerdict>> response = AppAPIFactory.getOrderVerdictService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<OrderVerdict> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // RepairPart ???
                referenceName = RepairPart.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<RepairPart>> response = AppAPIFactory.getRepairPartService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<RepairPart> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // RepairPartType ???
                referenceName = RepairPartType.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<RepairPartType>> response = AppAPIFactory
                            .getRepairPartTypeService().get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<RepairPartType> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Task ???
                referenceName = Task.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<Task>> response = AppAPIFactory.getTasksService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<Task> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Stages ???
                referenceName = Stage.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<Stage>> response = AppAPIFactory.getStageService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<Stage> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // StageStatus
                referenceName = StageStatus.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<StageStatus>> response = AppAPIFactory.getStageStatusService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<StageStatus> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // StageTemplate ???
                referenceName = StageTemplate.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<StageTemplate>> response = AppAPIFactory
                            .getStageTemplateService().get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<StageTemplate> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // StageType ???
                referenceName = StageType.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<StageType>> response = AppAPIFactory.getStageTypeService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<StageType> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // StageVerdict
                referenceName = StageVerdict.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<StageVerdict>> response = AppAPIFactory.getStageVerdictService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<StageVerdict> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // TaskStatus
                referenceName = TaskStatus.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<TaskStatus>> response = AppAPIFactory.getTaskStatusService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<TaskStatus> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // TaskTemplate ???
                referenceName = TaskTemplate.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<TaskTemplate>> response = AppAPIFactory.getTaskTemplateService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<TaskTemplate> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // TaskType ???
                referenceName = TaskType.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<TaskType>> response = AppAPIFactory.getTaskTypeService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<TaskType> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // TaskVerdict
                referenceName = TaskVerdict.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<TaskVerdict>> response = AppAPIFactory.getTaskVerdictService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<TaskVerdict> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Tool ???
                referenceName = Tool.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<Tool>> response = AppAPIFactory.getToolService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<Tool> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // ToolType ???
                referenceName = ToolType.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<ToolType>> response = AppAPIFactory.getToolTypeService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<ToolType> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // User ???

                // MediaFile (Пока тянем с прицелом на то что понадобится например смотреть фотки
                // предыдущих дефектов, например.)
                referenceName = MediaFile.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<MediaFile>> response = AppAPIFactory.getMediaFileService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<MediaFile> list = response.body();
                        // устанавливаем флаг того данные были уже отправлены на сервер
                        for (MediaFile file : list) {
                            file.setSent(true);
                        }

                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // AttributeType
                referenceName = AttributeType.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<AttributeType>> response = AppAPIFactory.getAttributeTypeService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<AttributeType> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // EquipmentAttribute
                referenceName = EquipmentAttribute.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<EquipmentAttribute>> response = AppAPIFactory.getEquipmentAttributeService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        // TODO: реализовать механизм проверки наличия изменённых данных локально
                        // при необходимости отбрасывать данные с сервера
                        List<EquipmentAttribute> list = response.body();
                        // сразу ставим флаг что они "отправлены", чтоб избежать их повторной отправки
                        for (EquipmentAttribute item : list) {
                            item.setSent(true);
                        }
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // DefectType
                referenceName = DefectType.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<DefectType>> response = AppAPIFactory.getDefectTypeService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<DefectType> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // DefectLevel
                referenceName = DefectLevel.class.getSimpleName();
                changedDate = ReferenceUpdate.lastChangedAsStr(referenceName);
                try {
                    Response<List<DefectLevel>> response = AppAPIFactory.getDefectLevelService()
                            .get(changedDate).execute();
                    if (response.isSuccessful()) {
                        List<DefectLevel> list = response.body();
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(list);
                        realm.commitTransaction();
                        ReferenceUpdate.saveReferenceData(referenceName, currentDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // гасим диалог обновления справочников
                if (dialog != null) {
                    dialog.dismiss();
                }

                realm.close();
            }
        });
        thread.start();
    }

    public static boolean isNeedDownload(File extDir, RealmObject obj, String localPath, boolean isRequery) {
        Realm realm = Realm.getDefaultInstance();
        String uuid = ((IToirDbObject) obj).getUuid();
        RealmObject dbObj = realm.where(obj.getClass()).equalTo("uuid", uuid).findFirst();
        long localChangedAt;

        // есть ли локальная запись
        if (dbObj != null) {
            localChangedAt = ((IToirDbObject) dbObj).getChangedAt().getTime();
            realm.close();
        } else {
            realm.close();
            return isRequery;
        }

        // есть ли локально файл
        String fileName = ((IToirDbObject) obj).getImageFile();
        if (fileName != null) {
            File file = new File(extDir.getAbsolutePath() + '/' + localPath, fileName);
            if (!file.exists()) {
                return isRequery;
            }
        } else {
            return false;
        }

        // есть ли изменения на сервере
        return localChangedAt < ((IToirDbObject) obj).getChangedAt().getTime();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.reference_layout, container, false);
        realmDB = Realm.getDefaultInstance();

        Spinner referenceSpinner = rootView.findViewById(R.id.simple_spinner);
        contentListView = rootView.findViewById(R.id.reference_listView);

        // получаем список справочников, разбиваем его на ключ:значение
        String[] referenceArray = getResources().getStringArray(R.array.references_array);
        String[] tmpValue;
        SortField item;
        ArrayList<SortField> referenceList = new ArrayList<>();
        for (String value : referenceArray) {
            tmpValue = value.split(":");
            item = new SortField(tmpValue[0], tmpValue[1]);
            referenceList.add(item);
        }

        Activity activity = getActivity();
        if (activity != null) {
            ArrayAdapter<SortField> referenceSpinnerAdapter = new ArrayAdapter<>(activity,
                    android.R.layout.simple_spinner_dropdown_item, referenceList);

            referenceSpinner.setAdapter(referenceSpinnerAdapter);
            ReferenceSpinnerListener referenceSpinnerListener = new ReferenceSpinnerListener();
            referenceSpinner.setOnItemSelectedListener(referenceSpinnerListener);
        }

        setHasOptionsMenu(true);
        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();

        return rootView;
    }

    private void fillListViewDocumentationType() {
        RealmResults<DocumentationType> documentationType;
        documentationType = realmDB.where(DocumentationType.class).findAll();
        DocumentationTypeAdapter documentationTypeAdapter = new DocumentationTypeAdapter(documentationType);
        contentListView.setAdapter(documentationTypeAdapter);
    }

    private void fillListViewEquipmentType() {
        RealmResults<EquipmentType> equipmentType;
        equipmentType = realmDB.where(EquipmentType.class).findAll();
        EquipmentTypeAdapter equipmentTypeAdapter = new EquipmentTypeAdapter(equipmentType);
        contentListView.setAdapter(equipmentTypeAdapter);
    }

    private void fillListViewCriticalType() {
        RealmResults<CriticalType> criticalType;
        criticalType = realmDB.where(CriticalType.class).findAll();
        CriticalTypeAdapter criticalTypeAdapter = new CriticalTypeAdapter(criticalType);
        contentListView.setAdapter(criticalTypeAdapter);
    }

    private void fillListViewAlertType() {
        RealmResults<AlertType> alertType;
        alertType = realmDB.where(AlertType.class).findAll();
        AlertTypeAdapter alertTypeAdapter = new AlertTypeAdapter(alertType);
        contentListView.setAdapter(alertTypeAdapter);
    }

    private void fillListViewOperationStatus() {
        RealmResults<OperationStatus> operationStatus;
        operationStatus = realmDB.where(OperationStatus.class).findAll();
        OperationStatusAdapter operationAdapter = new OperationStatusAdapter(operationStatus);
        contentListView.setAdapter(operationAdapter);
    }

    private void fillListViewOperationVerdict() {
        RealmResults<OperationVerdict> operationVerdict;
        operationVerdict = realmDB.where(OperationVerdict.class).findAll();
        OperationVerdictAdapter operationVerdictAdapter = new OperationVerdictAdapter(operationVerdict);
        contentListView.setAdapter(operationVerdictAdapter);
    }

    private void fillListViewObjectType() {
        RealmResults<ObjectType> objectType;
        objectType = realmDB.where(ObjectType.class).findAll();
        ObjectTypeAdapter objectAdapter = new ObjectTypeAdapter(objectType);
        contentListView.setAdapter(objectAdapter);
    }

    private void fillListViewDefectType() {
        RealmResults<DefectType> defectType;
        defectType = realmDB.where(DefectType.class).findAll();
        DefectTypeAdapter defectAdapter = new DefectTypeAdapter(defectType);
        contentListView.setAdapter(defectAdapter);
    }

    private void fillListViewDefectLevel() {
        RealmResults<DefectLevel> defectLevel;
        defectLevel = realmDB.where(DefectLevel.class).findAll();
        DefectLevelAdapter defectAdapter = new DefectLevelAdapter(defectLevel);
        contentListView.setAdapter(defectAdapter);
    }

    private void fillListViewOperationType() {
        RealmResults<OperationType> operationType;
        operationType = realmDB.where(OperationType.class).findAll();
        OperationTypeAdapter operationAdapter = new OperationTypeAdapter(operationType);
        contentListView.setAdapter(operationAdapter);
    }

    private void fillListViewTaskStatus() {
        RealmResults<TaskStatus> taskStatuses;
        taskStatuses = realmDB.where(TaskStatus.class).findAll();
        TaskStatusAdapter taskStatusAdapter = new TaskStatusAdapter(taskStatuses);
        contentListView.setAdapter(taskStatusAdapter);
    }

    private void fillListViewStageStatus() {
        RealmResults<StageStatus> stageStatuses;
        stageStatuses = realmDB.where(StageStatus.class).findAll();
        StageStatusAdapter stageStatusAdapter = new StageStatusAdapter(stageStatuses);
        contentListView.setAdapter(stageStatusAdapter);
    }

    private void fillListViewEquipmentStatus() {
        RealmResults<EquipmentStatus> equipmentStatuses;
        equipmentStatuses = realmDB.where(EquipmentStatus.class).findAll();
        EquipmentStatusAdapter equipmentAdapter = new EquipmentStatusAdapter(equipmentStatuses);
        contentListView.setAdapter(equipmentAdapter);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.support.v4.app.Fragment#onCreateOptionsMenu(android.view.Menu,
     * android.view.MenuInflater)
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);

        // добавляем элемент меню для обновления справочников
//        MenuItem getTask = menu.add("Обновить справочники");
//        getTask.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                Log.d(TAG, "Обновляем справочники.");
//                ProgressDialog dialog = new ProgressDialog(getActivity());
//
//
////				ReferenceServiceHelper rsh = new ReferenceServiceHelper(getActivity().getApplicationContext(), AppAPIFactory.Actions.ACTION_GET_ALL_REFERENCE);
////				getActivity().registerReceiver(mReceiverGetReference, mFilterGetReference);
////				rsh.getAll();
//                updateReferences(dialog);
//
//                // показываем диалог обновления справочников
//                dialog.setMessage("Получаем справочники");
//                dialog.setIndeterminate(true);
//                dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//                dialog.setCancelable(false);
//                dialog.setButton(DialogInterface.BUTTON_NEGATIVE,
//                        "Отмена", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog,
//                                                int which) {
////								getActivity().unregisterReceiver(mReceiverGetReference);
//                                Toast.makeText(getActivity(), "Обновление справочников отменено",
//                                        Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                    @Override
//                    public void onDismiss(DialogInterface dialogInterface) {
//                        Toast.makeText(getContext(), "Справочники обновлены", Toast.LENGTH_SHORT)
//                                .show();
//                    }
//                });
//                dialog.show();
//
//                return true;
//            }
//        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realmDB.close();
    }

    static class FilePath {
        String fileName;
        String urlPath;
        String localPath;

        FilePath(String name, String url, String local) {
            fileName = name;
            urlPath = url;
            localPath = local;
        }
    }

    /**
     * @author Dmitriy Logachov
     *         <p>
     *         Класс реализует обработку выбора элемента выпадающего списка
     *         справочников.
     *         </p>
     */
    private class ReferenceSpinnerListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position,
                                   long id) {

            SortField selectedItem = (SortField) parentView.getItemAtPosition(position);
            String selected = selectedItem.getField();

            switch (selected) {
                case DocumentationTypeAdapter.TABLE_NAME:
                    fillListViewDocumentationType();
                    break;
                case EquipmentTypeAdapter.TABLE_NAME:
                    fillListViewEquipmentType();
                    break;
                case CriticalTypeAdapter.TABLE_NAME:
                    fillListViewCriticalType();
                    break;
                case AlertTypeAdapter.TABLE_NAME:
                    fillListViewAlertType();
                    break;
                case OperationVerdictAdapter.TABLE_NAME:
                    fillListViewOperationVerdict();
                    break;
                case OperationTypeAdapter.TABLE_NAME:
                    fillListViewOperationType();
                    break;
                case OperationStatusAdapter.TABLE_NAME:
                    fillListViewOperationStatus();
                    break;
                case TaskStatusAdapter.TABLE_NAME:
                    fillListViewTaskStatus();
                    break;
                case StageStatusAdapter.TABLE_NAME:
                    fillListViewStageStatus();
                    break;
                case EquipmentStatusAdapter.TABLE_NAME:
                    fillListViewEquipmentStatus();
                    break;
                case ObjectTypeAdapter.TABLE_NAME:
                    fillListViewObjectType();
                    break;
                case DefectTypeAdapter.TABLE_NAME:
                    fillListViewDefectType();
                    break;
                case DefectLevelAdapter.TABLE_NAME:
                    fillListViewDefectLevel();
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parentView) {

        }
    }
}