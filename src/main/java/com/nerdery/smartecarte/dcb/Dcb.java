package com.nerdery.smartecarte.dcb;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Dcb {
	private byte[] macAddress;		// 6 bytes
	private byte[] internetAddress;	// 4 bytes
	private String manufacturer;	// 6 bytes
	private String productionDate;	// 3 bytes
	private String batchNumber;		// 2 bytes
	private String partNumber;		// 5 bytes
	private String hardwareVersion;	// 3 bytes
	private String firmwareVersion;	// 3 bytes
	
	private Integer columnNumber;	// 1 byte

	private List<DcbDevice> devices;
	
	public Dcb() {
		devices = new LinkedList<>();
	}

	public byte[] getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(byte[] macAddress) {
		this.macAddress = macAddress;
	}

	public byte[] getInternetAddress() {
		return internetAddress;
	}

	public void setInternetAddress(byte[] internetAddress) {
		this.internetAddress = internetAddress;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getProductionDate() {
		return productionDate;
	}

	public void setProductionDate(String productionDate) {
		this.productionDate = productionDate;
	}

	public String getBatchNumber() {
		return batchNumber;
	}

	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
	}

	public String getPartNumber() {
		return partNumber;
	}

	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}

	public String getHardwareVersion() {
		return hardwareVersion;
	}

	public void setHardwareVersion(String hardwareVersion) {
		this.hardwareVersion = hardwareVersion;
	}

	public String getFirmwareVersion() {
		return firmwareVersion;
	}

	public void setFirmwareVersion(String firmwareVersion) {
		this.firmwareVersion = firmwareVersion;
	}

	public Integer getColumnNumber() {
		return columnNumber;
	}

	public void setColumnNumber(Integer columnNumber) {
		this.columnNumber = columnNumber;
	}

	public List<DcbDevice> getDevices() {
		return devices;
	}

	public DcbDevice getDevice(int id) {
		for(DcbDevice d : devices) {
			if(d.getId() == id) {
				return d;
			}
		}
		return null;
	}

	public void addDevice(DcbDevice device) {
		devices.add(device);
	}

	public void setDevices(List<DcbDevice> devices) {
		this.devices = devices;
	}

	@Override
	public String toString() {
		return "Dcb [macAddress=" + Arrays.toString(macAddress) + ", internetAddress="
				+ Arrays.toString(internetAddress) + ", manufacturer=" + manufacturer + ", productionDate="
				+ productionDate + ", batchNumber=" + batchNumber + ", partNumber=" + partNumber + ", hardwareVersion="
				+ hardwareVersion + ", firmwareVersion=" + firmwareVersion + ", columnNumber=" + columnNumber
				+ ", devices=" + devices + "]";
	}

}
