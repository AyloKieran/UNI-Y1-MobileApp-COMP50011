const viewfinder = document.getElementById("viewfinder")
const preview = document.getElementById("preview")
var direction = localStorage.getItem("cameraDirection")
let globalstream = null
var play = true;
let constraints = {
    video: {
        width: {
            ideal: 1080
        },
        facingMode: {
            ideal: direction
        }
    },
    audio: false
};
const overlay = document.querySelector('.modal-overlay')
var closemodal = document.querySelectorAll('.modal-close')

overlay.addEventListener('click', toggleModal)
for (var i = 0; i < closemodal.length; i++) {
    closemodal[i].addEventListener('click', toggleModal)
}
document.onkeydown = function (evt) {
    evt = evt || window.event
    var isEscape = false
    if ("key" in evt) {
        isEscape = (evt.key === "Escape" || evt.key === "Esc")
    } else {
        isEscape = (evt.keyCode === 27)
    }
    if (isEscape && document.body.classList.contains('modal-active')) {
        toggleModal()
    }
};

function swapCamera() {
    if (localStorage.getItem("cameraDirection") == "environment") {
        localStorage.setItem("cameraDirection", "user")
    } else {
        localStorage.setItem("cameraDirection", "environment")
    }

    location.reload()
}

function takePhoto() {
    toggleModal()
    document.getElementById("lat").innerText = `Latitude: ${sessionStorage.getItem("userLat")}`
    document.getElementById("lon").innerText = `Longitude: ${sessionStorage.getItem("userLon")}`

    settings = globalstream.getVideoTracks()[0].getSettings()

    preview.width = settings["width"];
    preview.height = settings["height"];
    preview.getContext('2d').drawImage(viewfinder, 0, 0);
}

function toggleModal() {
    const body = document.querySelector('body')
    const modal = document.querySelector('.modal')
    modal.classList.toggle('opacity-0')
    modal.classList.toggle('pointer-events-none')
    body.classList.toggle('modal-active')
    if (play == true) {
        viewfinder.pause()
        play = false
    } else {
        viewfinder.play()
        play = true
    }
}

function createError(error) {
    holder = document.createElement("div")
    holder.setAttribute("class", "flex fixed top-0 mt-16  w-screen z-50 overflow-y-auto")
    holder.innerHTML = `
    <div class="w-11/12 md:max-w-md mx-auto bg-red-600 opacity-70 rounded shadow-lg">
        <div class="py-4 text-left px-6">
            <div class="flex justify-between items-center pb-3">
                <p class="text-2xl font-bold text-white">Error</p>
            </div>
            <p class="text-white">${error}</p>
            <p class="text-gray-300 text-sm">Ensure that permissions are granted and reload the page.</p>
        </div>
    </div>
    `

    document.querySelector("body").appendChild(holder)
}

function startLocation() {
    if (!navigator.geolocation) {
        createError("The geolocation feature is not available in your browser.")
    } else {
        navigator.geolocation.watchPosition(gotPosition, gotError)
    }
}

function gotPosition(position) {
    if (position == null) {
        createError("Null position");
        return;
    }
    sessionStorage.setItem("userLat", position.coords.latitude)
    sessionStorage.setItem("userLon", position.coords.longitude)
}

function gotError(error) {
    switch (error.code) {
        case error.PERMISSION_DENIED:
            createError("You have declined permission to use geolocation.");
            break;
        case error.POSITION_UNAVAILABLE:
            createError("Unable to determine your position using geolocation.");
            break;
        case error.TIMEOUT:
            createError("Unable to determine your position using geolocation within a given duration of time.");
            break;
        case error.UNKNOWN_ERROR:
            createError("An unknown error occurred when using geolocation.");
            break;
    }
}

navigator.mediaDevices.getUserMedia(constraints).then((stream) => {
    viewfinder.srcObject = stream;
    globalstream = stream

    if (direction == "user") {
        viewfinder.setAttribute("style", "transform: rotateY(180deg);")
    }

    viewfinder.addEventListener("loadedmetadata", () => {
        viewfinder.play();
    });
}).catch((error) => {
    createError(`Camera - ${error}`)
});

if (direction == null) {
    localStorage.setItem("cameraDirection", "user")
}

startLocation()