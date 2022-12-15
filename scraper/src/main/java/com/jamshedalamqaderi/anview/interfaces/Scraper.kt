package com.jamshedalamqaderi.anview.interfaces

interface Scraper<T> {
    fun scrape(): T
}