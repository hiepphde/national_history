package model;

import com.fasterxml.jackson.databind.JsonNode;
import util.JsonUtil;

public class Material {
    public String productCode;
    public String batch;
    public String plant;
    public String dateManufacture;
    public String expirationDate;
    public String vendor;
    public String visaNo;
    public String invoicingParty;
    public String packagingSense;
    public String supplierBatch;
    public String originCountry;
    public String prodLicenseNo;
    public Boolean freeOfCharge;
    public String certificates;
    public String manufacturePartner;
    public String entrustmentPartner;

    public static Material from(JsonNode item, JsonNode batch) {
        Material m = new Material();
        m.productCode = JsonUtil.get(item, "material");
        m.batch = JsonUtil.get(batch, "batchId");
        m.plant = JsonUtil.get(item, "plant");
        m.dateManufacture = JsonUtil.get(batch, "dateManufacture");
        m.expirationDate = JsonUtil.get(batch, "expirationDate");
        m.vendor = JsonUtil.get(batch, "vendor");
        m.visaNo = JsonUtil.get(batch, "visaNo");
        m.invoicingParty = JsonUtil.get(batch, "invoicingParty");
        m.packagingSense = JsonUtil.get(batch, "packagingSense");
        m.supplierBatch = JsonUtil.get(batch, "supplierBatch");
        m.originCountry = JsonUtil.get(batch, "originCountry");
        m.prodLicenseNo = JsonUtil.get(batch, "prodLicenseNo");
        m.freeOfCharge = batch.has("freeOfCharge") && batch.get("freeOfCharge").asBoolean();
        m.certificates = JsonUtil.get(batch, "certificates");
        m.manufacturePartner = JsonUtil.get(batch, "manufacturePartner");
        m.entrustmentPartner = JsonUtil.get(batch, "entrustmentPartner");
        return m;
    }
}
