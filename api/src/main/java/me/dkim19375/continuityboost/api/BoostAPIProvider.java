package me.dkim19375.continuityboost.api;

public class BoostAPIProvider {
    private static ContinuityBoostAPI api;

    public static ContinuityBoostAPI getApi() {
        return api;
    }

    public static void setApi(ContinuityBoostAPI api) {
        BoostAPIProvider.api = api;
    }
}
