package es.eig.myorder.ServerConnection

 object UrlCache {
    private val cache = mutableMapOf<String, String>()

    fun getUrl(key: String): String? = cache[key]

    fun saveUrl(key: String, url: String) {
        cache[key] = url
    }
}