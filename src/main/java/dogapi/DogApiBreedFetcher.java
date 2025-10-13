package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     *
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     * @throws BreedNotFoundException if the breed does not exist (or if the API call fails for any reason)
     */
    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException {
        if (breed == null) {
            throw new BreedNotFoundException("Breed is null");
        }
        String trimmedBreed = breed.trim().toLowerCase(Locale.ROOT);

        Request request = new Request.Builder()
                .url(String.format(("https://dog.ceo/api/breed/" + trimmedBreed + "/list")))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                throw new BreedNotFoundException("Failed to retrieve breed list");
            }

            String responseBody = response.body().string();
            JSONObject json = new JSONObject(responseBody);
            String status = json.optString("status");
            if (!status.equals("success")) {
                throw new BreedNotFoundException("Failed to retrieve breed list");
            }
            JSONArray arr = json.getJSONArray("message");
            List<String> subs = new ArrayList<>();
            for (int i = 0; i < arr.length(); i++) {
                subs.add(arr.getString(i));
            }
            return subs;

        } catch (IOException | JSONException e) {
            throw new BreedNotFoundException("Failed to retrieve breed list");
        }
    }
}

        // Complete this method based on its provided documentation
        //      and the documentation for the dog.ceo API. You may find it helpful
        //      to refer to the examples of using OkHttpClient from the last lab,
        //      as well as the code for parsing JSON responses.
        // return statement included so that the starter code can compile and run