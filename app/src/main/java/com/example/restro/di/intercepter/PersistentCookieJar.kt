package com.example.restro.di.intercepter

import android.webkit.CookieManager
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import java.net.HttpCookie
import java.util.concurrent.ConcurrentHashMap

object PersistentCookieJar : CookieJar {

    private val cookieStore = ConcurrentHashMap<String, MutableList<Cookie>>()
    private val cookieManager = CookieManager.getInstance()

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val cookies = cookieStore[url.host] ?: mutableListOf()
        // Filter out expired cookies
        val validCookies = cookies.filter { it.expiresAt >= System.currentTimeMillis() }
        cookieStore[url.host] = validCookies.toMutableList()
        return validCookies
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val host = url.host
        val currentCookies = cookieStore.getOrPut(host) { mutableListOf() }

        cookies.forEach { newCookie ->
            // Remove existing cookie with same name and domain
            currentCookies.removeAll { it.name == newCookie.name && it.domain == newCookie.domain }
            currentCookies.add(newCookie)

            // Sync to Android CookieManager for WebView
            val cookieString = HttpCookie(
                newCookie.name,
                newCookie.value
            ).apply {
                domain = newCookie.domain
                path = newCookie.path
                maxAge =
                    if (newCookie.persistent) (newCookie.expiresAt - System.currentTimeMillis()) / 1000 else -1
                secure = newCookie.secure
                isHttpOnly = newCookie.httpOnly
            }.toString()

            cookieManager.setCookie(newCookie.domain, cookieString)
        }

        cookieStore[host] = currentCookies
        cookieManager.flush() // persist cookies to storage
    }
}
