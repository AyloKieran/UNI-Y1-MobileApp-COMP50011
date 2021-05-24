function renderPost(title, description, likes, lat, lon, posted, img) {
    holder = document.createElement("div")
    holder.setAttribute("class", "flex flex-col bg-gray-400 dark:bg-gray-700 h-auto mb-2 shadow-xl rounded-lg")
    holder.innerHTML = `
    ${imageHandle(img, description)}
        <div class="flex p-2 pt-1">
            <div class="flex flex-col pr-0 flex-1">
                <h1 class="text-gray-800 dark:text-white font-bold text-2xl">
                    ${title}
                </h1>
                <p class="text-gray-700 dark:text-gray-300 text-sm">
                    ${description}
                </p>
                <br>
                <p class="text-gray-700 dark:text-gray-300 text-sm">
                    <span class="text-gray-800 dark:text-gray-200 font-bold">
                        Latitude: 
                    </span>
                    ${lat}
                </p>
                <p class="text-gray-700 dark:text-gray-300 text-sm">
                    <span class="text-gray-800 dark:text-gray-200 font-bold">
                        Longitude: 
                    </span>
                    ${lon}
                </p>
                <br>
                <span class="text-gray-800 dark:text-gray-200 font-bold">
                    Posted At: 
                </span>
                <p class="text-gray-700 dark:text-gray-300 text-sm">
                    ${posted}
                </p>
                <br>
                <button type="button" id="map" class="text-gray-800 focus:outline-none dark:text-gray-300 text-sm py-2 px-2 w-max rounded-md border border-gray-800 dark:border-gray-300 dark:hover:bg-gray-50 dark:hover:text-gray-700 hover:border-gray-600 hover:bg-gray-800 hover:text-gray-400">
                    <i class="fas fa-map-marker-alt pr-2"></i>View on Maps
                </button>
            </div>
            <p class="text-gray-700 dark:text-gray-300 text-right my-auto text-sm">${likes}<i class="far fa-thumbs-up pl-2"></i></p>
        </div>
        `

    postSection.appendChild(holder)

    document.getElementById("map").addEventListener("click", function(){
        window.open(`https://www.google.com/maps/search/?api=1&query=${lat},${lon}`)
    })
}

function handlePost() {
    if (!navigator.onLine) {
        addError("Offline", handleDate(localStorage.getItem("cacheUpdateAt")))
    }
    posts = JSON.parse(localStorage.getItem("cachedPosts"))
    var url = new URL(window.location.href)
    var postid = sessionStorage.getItem("displayPost")
    if (postid == null) {
        window.location.href = "index.html"
    }
    var post = posts.filter(x => x.id == postid)[0]
    renderPost(post.title, post.description, post.likes, post.lat, post.lon, handleDate(post.created_at), post.image.slice(0, -4))
}
window.addEventListener('load', function() {
    handlePost()
}, false)