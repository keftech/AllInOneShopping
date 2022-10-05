package kef.technology.allinoneshopping;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class WebViewModel extends ViewModel {

    private MutableLiveData<Boolean> showError;
    private MutableLiveData<String> errorMessage;

    public MutableLiveData<String> getErrorMesaage() {
        if(errorMessage == null)
            errorMessage = new MutableLiveData<>();
        return errorMessage;
    }

    public MutableLiveData<Boolean> getShowError() {
        if(showError == null)
            showError = new MutableLiveData<>();
        return showError;
    }
}
