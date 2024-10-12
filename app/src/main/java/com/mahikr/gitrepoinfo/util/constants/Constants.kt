package com.mahikr.gitrepoinfo.util.constants


/**** Constants
 * Defined HTTP, Paging, and Db/tables names, constants, end points, tokens
 */
object Constants {

    /** HTTP-CLIENT **/
    const val GIT_API_BASE_URL = "https://api.github.com/"

    //https://api.github.com/search/repositories?q=images&per_page=10&page=1
    //?q=images&per_page=10&page=1
    const val GIT_REPO_END_POINT = "search/repositories"

    //https://api.github.com/repos/oracle/docker-images/contributors
    const val REPO_CONTRIBUTORS_END_POINT = "https://api.github.com/repos/"

    //valid till Expires on Wed, Dec 11 2024.
    const val TEMP_TOKEN = "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"

    /** PAGING **/
    const val PER_PAGE_COUNT = 10

    /** DB **/
    //database name
    const val GIT_REPO_DATABASE = "gitrepo.db"
    //table name
    const val REPO_TABLE_NAME = "gitrepo_table"



}