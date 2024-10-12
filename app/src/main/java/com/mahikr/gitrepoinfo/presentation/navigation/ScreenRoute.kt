package com.mahikr.gitrepoinfo.presentation.navigation

sealed class ScreenRoute(val route: String) {
    data object Main : ScreenRoute(Route.HOME_SCREEN_ROUTE.name)
    data object Detail : ScreenRoute(Route.DETAIL_SCREEN_ROUTE.name){
        fun getPath() = this.route+"/{id}"
        fun navigate(id: Int)  = this.route+"/$id"
    }
}