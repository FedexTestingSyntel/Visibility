package EMASS;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import API_Functions.General_API_Calls;

public class eMASS_session_cookie {

/*	public static String getEMASSCookie(String URL, String Credentials) {
		HttpPost httppost = new HttpPost(URL);

		httppost.addHeader("Content-Type", "application/x-www-form-urlencoded");
		httppost.addHeader("Accept", "application/json");
		httppost.addHeader("Content-Type", "application/json");

		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("username", Credentials));
		urlParameters.add(new BasicNameValuePair("password", Credentials));
		// Need to determine how this value is generated.
		urlParameters.add(new BasicNameValuePair("request_id", "8067903622302088728"));

		urlParameters.add(new BasicNameValuePair("OAM_REQ", "VERSION_4~A8fP3DN3H5doCQNc5rdsWN%2fXOxz8r7xcLTN7%2brWexyjJ6tSU1DpcW9DP4zgLopZ5jSvtBskR0otxshXwWiqdb6CQ7eGXZIg9r95vawpTCZlVw%2bwX6nKI4z1P1oGZYV8De4RAm8pejsLCPEVVPPmbAzJAztU5KTmXL2YQDhHhvhKeNcHAjgBxLcUdQ7qh%2bEvXjBmdtcmo5NesOSQAypVF0AaTSDwXblrI4LuUiqmW7d%2b7TWADhinUIMekZOvxHsH6iA%2b08%2bBlmv3wrBuvbVIbiatuwCP%2fNEF5jmIVl3aiCgYdJYjL45P7PNCW5g53Enh4H%2fNcbeBBE2DuzVZLG3z00cs4qy8JK33pN9ny33Vjdps4G9cG8qltnuthWlX2uku9bcCXXJpsUOZVmlZ7I1BwQpgSt1C7rTT8K2V80BzuXduHOQ9dlFDhRdwZeZI33HUj5am7kT2ujwqsvSl5cRP5i%2fgNABP7amiLArE1HIG8nbqTtP%2fKCEDB1A7x%2bCgh214Ni3PGYJbPSq97nIH7yMy83D%2fcAjs8bFkIr9cTqpcqkEb23aTaA3b2VOViYQIuNNKL%2bkSTia%2brMAXo%2fVvrdhmwv7ABPMu4ea4N3AHkWiY6d4vscAv6GHCaJWz3Bg%2bCAbL6EaV9qs50A5N3UUzfwNMBhQqZ5mnZ5w1MawUbWL1XCJN5OMqnlMHvqlGh3U%2bs9fk%2f0AvmhcCcyQIpk6fuZa5j6iTEgSi7J1gbW%2f7uYkTMa0H%2fIsRZCY7CUnLfwsYvjpCiz%2bwrH%2b44lwI7AU2Pdw8yG%2fCDTiNihrHQMVB8kzQIVTQQ6aAwhpxjQApHzgSmnwwrqr2QpiOFYmzSchOAEHa8iN7FdawaLCIRXZBrqOscvUG5s3JnYHmhtvbtTNO%2fLTYY1PtrvOososaaW9GoFbfLcuwnxOOwgi8e5Z%2fNnWf67n1aB9UQr0PcgCgiGyFKHwjDUCDDlbo6UFJwe%2bSQRhotqpQl1ig5iLdjPGVmwPNK6tLiHlN%2fOJH7pCREABPsbW1LcH7BlktNUiCHCEM%2bsaLudHRsIfgs7DjNNkBitPkSS3pskW0RxArpa%2bHJd9cmx2xTSv11k9LfqGzjUaq4Hrule3RUjPUy0j3Yh3yuECmQdAfkmqQew81W8RrGFsGAsgRnIBr7JJBEnfUWV0GxW%2bdi%2faPqRznpbF%2f8qjUpjrykAC3Yq%2bIxYpuiYr0hbp%2fXUpLykgJ0mLb7RhVoPQ1AxvOME82gONkPiftMB2A5L6iISz9RgkFsxJSpBiclhQe5nBb7k8xj2gLl4SxpqRCTg5CcG5t0m1qBog0CjIKvKExO5fWK2RL2xo82rjHLVqJjmS45mQo2JYJT0vGiZg0KMP83YjO12KbjwTOpAFCgtRq4HNMw2gv2fJx7SwDsmGiIlE%2bXQux6EjUoGWP5KxTpRBHXfpcoVu29p93MiJyNDg0Q9lr0actUZquv6P%2fHnq%2fID1SGglITBx19x33Qrt2IHP2BFBlwEpenSfdqn9cRS4pLNVEiDCRZluWGvP0TZo%2bWfxqmqcWwQ%2fgC7Okvo3LsV1EBL6LqaaaFfUqKZNBGfuf%2b9LtG4E3EIDxQrvoWPpbP6whohjHV57iv8eVTB87xzbTTx6uI3yliQEhCJE5tbLkMDVZ73L8KK%2bqBDQRtNrLyHrc%2bK75N1LWfQZfw0JIm1Csa0POk1AnGM5JcZJVQMw6gDSeuwfCuVl4l%2fnobri%2f7LR7YzL8Fjv%2f%2f8IhOZbKkoqesv7fBpXBMU2JImOglAf2WnJqYGOVWkhlfczXXXn6%2b4XIuEjSyydq0yxREPMmI8ihmiaC%2bBjLi5RMy7NKKB9IFKo8h%2br1szkSW05ZyAWB1HCRN8hHHmBwg0Li2%2f7Ms%2bnckoPSucLaw0sMnvM38a9VDEmgEo2E5GoJ3re2ksN%2funEVxzT18lvx4bZCanrHDXAUraHJnEJvWNicuBmvAFctAIVVCz2nPHcIpKgXPw9wASH9Yvi5be%2fCYkWiwgt%2bjoZn2JczXQqo2z0lStt5nNRWBdmG0EHLpDr5G%2bQh7QnIZz94X%2bib0BuEDvVzJOfwEMmoCvbbLOND8oG%2bCxcCSuHqrkb47FVyG7xGkh%2fsaZQE7mrM0"));
		urlParameters.add(new BasicNameValuePair("lang", "en_US"));
		

		try {
			httppost.setEntity(new UrlEncodedFormEntity(urlParameters));
			HttpClient httpclient = HttpClients.createDefault();
			HttpResponse Response = httpclient.execute(httppost);
			Header[] Headers = Response.getAllHeaders();
			
			String Cookies = "";
			String RequestHeaders = "";
			for (Header Header : Headers) {
				if (Header.getName().contentEquals("Set-Cookie")) {
					Cookies += Header.toString().replace("Set-Cookie: ", ""); 
				}
				RequestHeaders += Header + "___";
			}
				
			String MethodName = "EMASSLogin";
			General_API_Calls.Print_Out_API_Call(MethodName, httppost.toString(), RequestHeaders, Credentials, Response.toString());

			return Cookies;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}	  
	}*/
	
	public static String getEMASSCookie(String URL, String Credentials) {
		HttpPost httppost = new HttpPost(URL);

		httppost.addHeader("Content-Type", "application/x-www-form-urlencoded");
		httppost.addHeader("Accept", "application/json");
		httppost.addHeader("Content-Type", "application/json");

		List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
		urlParameters.add(new BasicNameValuePair("username", Credentials));
		urlParameters.add(new BasicNameValuePair("password", Credentials));
		// Need to determine how this value is generated.
		urlParameters.add(new BasicNameValuePair("request_id", "8067903622302088728"));

		urlParameters.add(new BasicNameValuePair("OAM_REQ", "VERSION_4~A8fP3DN3H5doCQNc5rdsWN%2fXOxz8r7xcLTN7%2brWexyjJ6tSU1DpcW9DP4zgLopZ5jSvtBskR0otxshXwWiqdb6CQ7eGXZIg9r95vawpTCZlVw%2bwX6nKI4z1P1oGZYV8De4RAm8pejsLCPEVVPPmbAzJAztU5KTmXL2YQDhHhvhKeNcHAjgBxLcUdQ7qh%2bEvXjBmdtcmo5NesOSQAypVF0AaTSDwXblrI4LuUiqmW7d%2b7TWADhinUIMekZOvxHsH6iA%2b08%2bBlmv3wrBuvbVIbiatuwCP%2fNEF5jmIVl3aiCgYdJYjL45P7PNCW5g53Enh4H%2fNcbeBBE2DuzVZLG3z00cs4qy8JK33pN9ny33Vjdps4G9cG8qltnuthWlX2uku9bcCXXJpsUOZVmlZ7I1BwQpgSt1C7rTT8K2V80BzuXduHOQ9dlFDhRdwZeZI33HUj5am7kT2ujwqsvSl5cRP5i%2fgNABP7amiLArE1HIG8nbqTtP%2fKCEDB1A7x%2bCgh214Ni3PGYJbPSq97nIH7yMy83D%2fcAjs8bFkIr9cTqpcqkEb23aTaA3b2VOViYQIuNNKL%2bkSTia%2brMAXo%2fVvrdhmwv7ABPMu4ea4N3AHkWiY6d4vscAv6GHCaJWz3Bg%2bCAbL6EaV9qs50A5N3UUzfwNMBhQqZ5mnZ5w1MawUbWL1XCJN5OMqnlMHvqlGh3U%2bs9fk%2f0AvmhcCcyQIpk6fuZa5j6iTEgSi7J1gbW%2f7uYkTMa0H%2fIsRZCY7CUnLfwsYvjpCiz%2bwrH%2b44lwI7AU2Pdw8yG%2fCDTiNihrHQMVB8kzQIVTQQ6aAwhpxjQApHzgSmnwwrqr2QpiOFYmzSchOAEHa8iN7FdawaLCIRXZBrqOscvUG5s3JnYHmhtvbtTNO%2fLTYY1PtrvOososaaW9GoFbfLcuwnxOOwgi8e5Z%2fNnWf67n1aB9UQr0PcgCgiGyFKHwjDUCDDlbo6UFJwe%2bSQRhotqpQl1ig5iLdjPGVmwPNK6tLiHlN%2fOJH7pCREABPsbW1LcH7BlktNUiCHCEM%2bsaLudHRsIfgs7DjNNkBitPkSS3pskW0RxArpa%2bHJd9cmx2xTSv11k9LfqGzjUaq4Hrule3RUjPUy0j3Yh3yuECmQdAfkmqQew81W8RrGFsGAsgRnIBr7JJBEnfUWV0GxW%2bdi%2faPqRznpbF%2f8qjUpjrykAC3Yq%2bIxYpuiYr0hbp%2fXUpLykgJ0mLb7RhVoPQ1AxvOME82gONkPiftMB2A5L6iISz9RgkFsxJSpBiclhQe5nBb7k8xj2gLl4SxpqRCTg5CcG5t0m1qBog0CjIKvKExO5fWK2RL2xo82rjHLVqJjmS45mQo2JYJT0vGiZg0KMP83YjO12KbjwTOpAFCgtRq4HNMw2gv2fJx7SwDsmGiIlE%2bXQux6EjUoGWP5KxTpRBHXfpcoVu29p93MiJyNDg0Q9lr0actUZquv6P%2fHnq%2fID1SGglITBx19x33Qrt2IHP2BFBlwEpenSfdqn9cRS4pLNVEiDCRZluWGvP0TZo%2bWfxqmqcWwQ%2fgC7Okvo3LsV1EBL6LqaaaFfUqKZNBGfuf%2b9LtG4E3EIDxQrvoWPpbP6whohjHV57iv8eVTB87xzbTTx6uI3yliQEhCJE5tbLkMDVZ73L8KK%2bqBDQRtNrLyHrc%2bK75N1LWfQZfw0JIm1Csa0POk1AnGM5JcZJVQMw6gDSeuwfCuVl4l%2fnobri%2f7LR7YzL8Fjv%2f%2f8IhOZbKkoqesv7fBpXBMU2JImOglAf2WnJqYGOVWkhlfczXXXn6%2b4XIuEjSyydq0yxREPMmI8ihmiaC%2bBjLi5RMy7NKKB9IFKo8h%2br1szkSW05ZyAWB1HCRN8hHHmBwg0Li2%2f7Ms%2bnckoPSucLaw0sMnvM38a9VDEmgEo2E5GoJ3re2ksN%2funEVxzT18lvx4bZCanrHDXAUraHJnEJvWNicuBmvAFctAIVVCz2nPHcIpKgXPw9wASH9Yvi5be%2fCYkWiwgt%2bjoZn2JczXQqo2z0lStt5nNRWBdmG0EHLpDr5G%2bQh7QnIZz94X%2bib0BuEDvVzJOfwEMmoCvbbLOND8oG%2bCxcCSuHqrkb47FVyG7xGkh%2fsaZQE7mrM0"));
		urlParameters.add(new BasicNameValuePair("lang", "en_US"));
		

		try {
			httppost.setEntity(new UrlEncodedFormEntity(urlParameters));
			HttpClient httpclient = HttpClients.createDefault();
			HttpResponse Response = httpclient.execute(httppost);
			Header[] Headers = Response.getAllHeaders();
			
			String Cookies = "";
			String RequestHeaders = "";
			for (Header Header : Headers) {
				if (Header.getName().contentEquals("Set-Cookie")) {
					Cookies += Header.toString().replace("Set-Cookie: ", ""); 
				}
				RequestHeaders += Header + "___";
			}
				
			String MethodName = "EMASSLogin";
			General_API_Calls.Print_Out_API_Call(MethodName, httppost.toString(), RequestHeaders, Credentials, Response.toString());

			Cookies = "ShipmentGUISessionID=P018O-sM-Y6TMHjkIvfgMwKRQZnSWXXt71MFQqCc-RxSIKDpXVg7!1402392689; eShipmentGUI.userId=832614;";
			
			return Cookies;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}	  
	}
	
}
