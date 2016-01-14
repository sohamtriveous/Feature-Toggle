package cc.soham.toggle.objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Device {

    @SerializedName("manufacturer")
    @Expose
    private String manufacturer;
    @SerializedName("model")
    @Expose
    private String model;

    /**
     * 
     * @return
     *     The manufacturer
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * 
     * @param manufacturer
     *     The manufacturer
     */
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    /**
     * 
     * @return
     *     The model
     */
    public String getModel() {
        return model;
    }

    /**
     * 
     * @param model
     *     The model
     */
    public void setModel(String model) {
        this.model = model;
    }

}
