package dev.dejoe.nougall

object Constants {
    const val TMDB_BASE_URL = "https://api.themoviedb.org/3/"
    object Screen {
        const val TrendingGraph = "trending_graph"
        const val Home = "home"
        const val TrendingList = "trending_list"
        const val TrendingListWithArg = "trending_list?timeWindow={timeWindow}"

        const val Favorites = "favorites"
        const val Info = "info"
        fun movieDetailsRoute(movieId: Int): String {
            return "details/$movieId"
        }
        fun trendingListRoute(timeWindow: String): String {
            return "trending_list?timeWindow=$timeWindow"
        }
    }
}