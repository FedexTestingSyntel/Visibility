package API_Functions;

import com.google.api.client.http.GenericUrl;

public class GitHubUrl extends GenericUrl {

    public GitHubUrl(String encodedUrl) {
        super(encodedUrl);
    }

}