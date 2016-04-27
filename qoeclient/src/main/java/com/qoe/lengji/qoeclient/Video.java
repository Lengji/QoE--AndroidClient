package com.qoe.lengji.qoeclient;

import android.net.Uri;

public class Video {

	private String Title;
	private String Description;
	private Uri Uri_UHD;
	private Uri Uri_HD;
	private Uri Uri_SD;

	public Video(String title, String description, Uri uri_UHD, Uri uri_HD, Uri uri_SD) {
		Title = title;
		Description = description;
		Uri_UHD = uri_UHD;
		Uri_HD = uri_HD;
		Uri_SD = uri_SD;
	}

	@Override
	public String toString() {
		return "Video{" +
				"Title='" + Title + '\'' +
				", Description='" + Description + '\'' +
				'}';
	}

	public String getTitle() {
		return Title;
	}

	public void setTitle(String title) {
		Title = title;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public Uri getUri_UHD() {
		return Uri_UHD;
	}

	public void setUri_UHD(Uri uri_UHD) {
		Uri_UHD = uri_UHD;
	}

	public Uri getUri_HD() {
		return Uri_HD;
	}

	public void setUri_HD(Uri uri_HD) {
		Uri_HD = uri_HD;
	}

	public Uri getUri_SD() {
		return Uri_SD;
	}

	public void setUri_SD(Uri uri_SD) {
		Uri_SD = uri_SD;
	}
}