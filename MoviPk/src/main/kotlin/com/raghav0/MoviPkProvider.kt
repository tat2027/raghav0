package com.raghav0

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*
import org.jsoup.nodes.Element

class MoviPkProvider : MainAPI() {
    override var mainUrl = "https://movi.pk"
    override var name = "MoviPk"
    override val hasMainPage = true
    override var lang = "ur"
    override val supportedTypes = setOf(
        TvType.Movie,
        TvType.TvSeries
    )

    override suspend fun getMainPage(page: Int, request: HomePageRequest): HomePageResponse {
        val document = app.get(mainUrl).document
        val home = document.select("div.items").mapNotNull { it.toSearchResult() }
        return HomePageResponse(arrayListOf(HomePageList("Trending Movies", home)))
    }

    private fun Element.toSearchResult(): SearchResponse? {
        val title = this.selectFirst("h2 a")?.text() ?: return null
        val href = fixUrl(this.selectFirst("h2 a")?.attr("href") ?: return null)
        val posterUrl = fixUrl(this.selectFirst("img")?.attr("src"))
        
        return newMovieSearchResponse(title, href, TvType.Movie) {
            this.posterUrl = posterUrl
        }
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val document = app.get("$mainUrl/?s=$query").document
        return document.select("div.items").mapNotNull { it.toSearchResult() }
    }

    override suspend fun load(url: String): LoadResponse {
        val document = app.get(url).document
        val title = document.selectFirst("h1")?.text() ?: "No Title"
        val poster = fixUrl(document.selectFirst("img.poster")?.attr("src"))
        val description = document.selectFirst("div.description")?.text()

        return newMovieLoadResponse(title, url, TvType.Movie, url) {
            this.posterUrl = poster
            this.plot = description
        }
    }

    override suspend fun loadLinks(
        data: String,
        isCams: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        return true
    }
}
