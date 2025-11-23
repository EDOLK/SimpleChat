
setInterval(refreshCookies, 100 * 60 * 30)

async function refreshCookies() {
    await fetch(`/api/user/refresh`,
        {
            method: 'HEAD',
        }
    );
}
