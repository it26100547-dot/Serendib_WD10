// Serendib Core Application JS
// When running via file://, redirect to Spring Boot server if active
if (window.location.protocol === 'file:') {
    fetch('http://localhost:8080/', { method: 'HEAD', mode: 'no-cors' })
        .then(() => {
            const fileName = window.location.pathname.split('/').pop() || 'index.html';
            window.location.replace(`http://localhost:8080/${fileName}${window.location.search}${window.location.hash}`);
        })
        .catch(() => {
            console.info('[Serendib] Running in local preview mode. Start run-backend.bat for full Spring Boot features.');
        });
}

const DATA = window.CEYLON_DATA;

function qs(k) {
    return new URLSearchParams(location.search).get(k);
}

function money(v) {
    return '$' + Number(v || 0).toFixed(0);
}

function stars(v) {
    return '★'.repeat(Math.round(v || 5));
}

function getCurrentUser() {
    try {
        const u = localStorage.getItem('serendib_user');
        return u ? JSON.parse(u) : null;
    } catch (e) {
        return null;
    }
}

function setCurrentUser(user) {
    if (!user) localStorage.removeItem('serendib_user');
    else localStorage.setItem('serendib_user', JSON.stringify(user));
}

async function logoutUser(redirectTo = 'login.html') {
    // Clear the browser copy immediately, then invalidate the HttpOnly server session.
    setCurrentUser(null);
    try {
        if (window.SerendibApi?.auth?.logout) {
            await window.SerendibApi.auth.logout();
        } else {
            await fetch('/api/auth/logout', { method: 'POST', credentials: 'same-origin', cache: 'no-store' });
        }
    } catch (e) {
        console.info('[Serendib] Logout request failed, but local account state was cleared.');
    }
    // replace() prevents the profile/admin page being restored by the Back button.
    window.location.replace(redirectTo);
}

async function syncSessionUser() {
    if (!window.SerendibApi?.auth?.me) return getCurrentUser();
    try {
        const res = await window.SerendibApi.auth.me();
        if (res?.data) {
            const before = JSON.stringify(getCurrentUser() || {});
            setCurrentUser(res.data);
            if (before !== JSON.stringify(res.data)) {
                header();
                footer();
            }
            return res.data;
        }
    } catch (e) {
        if (e.status === 401) {
            const hadUser = !!getCurrentUser();
            setCurrentUser(null);
            if (hadUser) { header(); footer(); }
        }
    }
    return null;
}


function header() {
    const path = location.pathname.split('/').pop() || 'index.html';
    const inAdmin = location.pathname.includes('/admin/');
    if (inAdmin) return;

    const nav = [
        ['index.html', 'HOME'],
        ['packages.html', 'TOUR PACKAGES'],
        ['destinations.html', 'DESTINATIONS'],
        ['transport.html', 'TRANSPORT'],
        ['hotels.html', 'HOTELS']
    ];

    const user = getCurrentUser();
    const userButtonHtml = user
        ? `<a class="user-btn logged-in" href="profile.html" title="${user.firstName || 'Profile'}"><i class="fa-solid fa-user-check"></i> <span class="user-name" style="font-size:12px;margin-left:4px">${user.firstName || 'User'}</span></a>`
        : `<a class="user-btn" href="login.html" title="Login / Register"><i class="fa-regular fa-user"></i></a>`;

    const el = document.getElementById('siteHeader');
    if (!el) return;

    el.innerHTML = `
        <header class="site-header">
            <div class="nav-wrap">
                <a class="brand" href="index.html">
                    <span class="brand-mark">S</span>
                    <span>Serendib</span>
                </a>
                <nav class="desktop-nav">
                    ${nav.map(([h, n]) => `<a class="${path === h ? 'active' : ''}" href="${h}">${n}</a>`).join('')}
                </nav>
                <div style="display:flex;align-items:center;gap:12px;">
                    ${userButtonHtml}
                    <button class="menu-btn" id="menuBtn"><i class="fa-solid fa-bars"></i></button>
                </div>
            </div>
        </header>
        <nav class="mobile-menu" id="mobileMenu">
            ${nav.map(([h, n]) => `<a href="${h}">${n}</a>`).join('')}
            ${user ? '<a href="profile.html">MY PROFILE</a><a href="#" onclick="logoutUser();return false;">LOGOUT</a>' : '<a href="login.html">LOGIN / REGISTER</a>'}
        </nav>
    `;

    document.getElementById('menuBtn')?.addEventListener('click', () => {
        document.getElementById('mobileMenu').classList.toggle('open');
    });
}

function footer() {
    const user = getCurrentUser();
    const el = document.getElementById('siteFooter');
    if (!el) return;
    el.innerHTML = `
        <footer class="footer">
            <div class="container">
                <div class="footer-grid">
                    <div>
                        <h3>Serendib</h3>
                        <p>A full-stack Sri Lankan tourism management system powered by Java Spring Boot MVC architecture, OOP principles (Encapsulation, Inheritance, Polymorphism), and Notepad NoSQL document storage.</p>
                    </div>
                    <div>
                        <h3>Explore</h3>
                        <a href="packages.html">Tour Packages</a>
                        <a href="destinations.html">Destinations</a>
                        <a href="hotels.html">Hotels</a>
                    </div>
                    <div>
                        <h3>Travel</h3>
                        <a href="transport.html">Transport</a>
                        <a href="bookings.html">My Bookings</a>
                    </div>
                    <div>
                        <h3>Account & Admin</h3>
                        <a href="login.html">Sign In</a>
                        <a href="register.html">Sign Up</a>
                        ${user && user.role === 'ADMIN' ? '<a href="admin/dashboard.html">Admin Dashboard</a>' : ''}
                    </div>
                </div>
                <div class="footer-bottom">© 2026 Serendib · Powered by Java Spring Boot &amp; Notepad NoSQL Storage</div>
            </div>
        </footer>
    `;
}

// Tour Package Cards: combination of destinations, transport and hotel
function packageCard(p) {
    const stopsCount = (p.destinations && Array.isArray(p.destinations)) ? p.destinations.length : (p.destinationIds ? p.destinationIds.length : 3);
    const comboInfo = [
        p.transportName ? `<span title="Included Transport"><i class="fa-solid fa-van-shuttle"></i> ${p.transportName}</span>` : '',
        p.hotelName ? `<span title="Recommended Hotel"><i class="fa-solid fa-hotel"></i> ${p.hotelName}</span>` : ''
    ].filter(Boolean).join('');

    const comboSection = comboInfo ? `<div class="package-combo-tags" style="display:flex;flex-wrap:wrap;gap:8px;font-size:11px;color:#8ab4f8;margin:8px 0;background:rgba(255,255,255,0.05);padding:6px 10px;border-radius:6px;">${comboInfo}</div>` : '';

    return `
        <article class="card">
            <div class="card-media">
                <img src="${p.cover || p.image || 'assets/images/hero-sigiriya-gardens.jpg'}" alt="${p.name}">
                ${p.featured ? '<span class="badge">FEATURED</span>' : ''}
            </div>
            <div class="card-body">
                <span class="eyebrow">${p.category || 'All-In-One Package'}</span>
                <h3>${p.name}</h3>
                <p>${p.summary || p.description || ''}</p>
                ${comboSection}
                <div class="meta">
                    <span><i class="fa-regular fa-clock"></i> ${p.days || 7} Days</span>
                    <span><i class="fa-solid fa-location-dot"></i> ${stopsCount} Stops</span>
                    <span>★ ${p.rating || 4.9}</span>
                </div>
                <div class="price">
                    ${money(p.price)} <small>${p.priceUnit || 'per person'}</small>
                    ${p.normalPrice && p.normalPrice > p.price ? `<span style="font-size:13px;text-decoration:line-through;color:#888;margin-left:8px">${money(p.normalPrice)}</span>` : ''}
                </div>
                <div class="card-actions">
                    <a class="btn btn-dark" href="package-details.html?id=${p.id}">View Package</a>
                    <button class="btn btn-outline wishlist" data-type="package" data-id="${p.id}">♡</button>
                </div>
            </div>
        </article>
    `;
}

// 1. Destination Cards (Requirement 1)
function destinationCard(d) {
    const attrCount = (d.places && Array.isArray(d.places) && d.places.length) ? d.places.length : ((d.attractions && Array.isArray(d.attractions)) ? d.attractions.length : 0);
    return `
        <article class="card">
            <div class="card-media">
                <img src="${d.image || 'assets/images/hero-sigiriya-gardens.jpg'}" alt="${d.name}">
                <span class="badge">${d.region || 'Sri Lanka'}</span>
            </div>
            <div class="card-body">
                <span class="eyebrow">${d.category || 'Destination'}</span>
                <h3>${d.name}</h3>
                <p>${d.summary || d.description || ''}</p>
                <div class="meta">
                    <span><i class="fa-solid fa-map-pin"></i> ${attrCount} attractions</span>
                </div>
                <div class="card-actions">
                    <a class="btn btn-dark" href="destination-details.html?id=${d.id}">Explore Destination</a>
                </div>
            </div>
        </article>
    `;
}

// 2. Transport Cards (Requirement 2)
function vehicleCard(v) {
    const statusClass = (v.status || '').toLowerCase() === 'available' ? 'status-avail' : 'status-booked';
    return `
        <article class="card">
            <div class="card-media">
                <img src="${v.image || 'assets/images/hero-elephants.jpg'}" alt="${v.name}">
                <span class="badge ${statusClass}">${v.status || 'Available'}</span>
            </div>
            <div class="card-body">
                <span class="eyebrow">${v.type || 'Vehicle'}</span>
                <h3>${v.name}</h3>
                <p>${v.description || v.summary || ''}</p>
                <div class="meta">
                    <span><i class="fa-solid fa-user-group"></i> ${v.capacity || 4} passengers</span>
                    <span>${v.ac ? '<i class="fa-solid fa-snowflake"></i> A/C' : 'Non A/C'}</span>
                    <span>${v.driver ? '<i class="fa-solid fa-id-card"></i> Driver' : ''}</span>
                </div>
                <div class="price">${money(v.price)} <small>/ day</small></div>
                <div class="card-actions">
                    <a class="btn btn-dark" href="vehicle-details.html?id=${v.id}">View Vehicle</a>
                    <a class="btn btn-primary" href="vehicle-details.html?id=${v.id}#book">Book</a>
                </div>
            </div>
        </article>
    `;
}

// 3. Hotel Cards (Requirement 3)
function hotelCard(h) {
    const starCount = h.stars || Math.round(h.rating || 4);
    const roomCount = (h.rooms && Array.isArray(h.rooms)) ? h.rooms.length : 3;
    const price = h.fromPrice || h.from || 65;
    return `
        <article class="card">
            <div class="card-media">
                <img src="${h.image || 'assets/images/hero-sigiriya-aerial.jpg'}" alt="${h.name}">
                <span class="badge">${'★'.repeat(Math.min(5, Math.max(1, starCount)))}</span>
            </div>
            <div class="card-body">
                <span class="eyebrow">${h.destination || 'Sri Lanka'}</span>
                <h3>${h.name}</h3>
                <p>${h.description || h.summary || ''}</p>
                <div class="meta">
                    <span>★ ${h.rating || 4.5}</span>
                    <span><i class="fa-solid fa-bed"></i> ${roomCount} room types</span>
                </div>
                <div class="price">From ${money(price)} <small>/ night</small></div>
                <div class="card-actions">
                    <a class="btn btn-dark" href="hotel-details.html?id=${h.id}">View Hotel</a>
                    <a class="btn btn-primary" href="hotel-details.html?id=${h.id}#book">Book</a>
                </div>
            </div>
        </article>
    `;
}

function wireWishlist() {
    document.querySelectorAll('.wishlist').forEach(b => {
        b.onclick = () => {
            b.textContent = b.textContent === '♡' ? '♥' : '♡';
            b.style.color = b.textContent === '♥' ? '#ef3d4f' : '';
        };
    });
}

header();
footer();
// Verify localStorage against the real server session on every page.
syncSessionUser();

// Browsers can restore a page from the back/forward cache after logout. Recheck
// the real server session whenever a page becomes visible again so a signed-out
// user never sees stale account/admin navigation.
window.addEventListener('pageshow', () => {
    syncSessionUser();
});

// Hero image collage rotation
function initHeroCollage() {
    const root = document.getElementById('heroCollage');
    if (!root) return;
    const images = [
        'assets/images/hero-sigiriya-gardens.jpg',
        'assets/images/hero-anuradhapura-sunrise.jpg',
        'assets/images/hero-sigiriya-aerial.jpg',
        'assets/images/hero-elephants.jpg',
        'assets/images/hero-nine-arch-train.jpg'
    ];
    images.forEach(src => { const i = new Image(); i.src = src; });
    const frames = [...root.querySelectorAll('[data-hero-frame]')];
    let round = 0;
    function swapFrame(frame, frameIndex) {
        const layers = [...frame.querySelectorAll('.hero-layer')];
        const current = layers.findIndex(x => x.classList.contains('active'));
        const next = current === 0 ? 1 : 0;
        const imageIndex = (frameIndex + round + 1) % images.length;
        const incoming = layers[next];
        incoming.src = images[imageIndex];
        incoming.classList.add('active');
        layers[current].classList.remove('active');
        frame.classList.remove('is-pulsing');
        void frame.offsetWidth;
        frame.classList.add('is-pulsing');
    }
    function rotate() {
        round = (round + 1) % images.length;
        frames.forEach((frame, index) => setTimeout(() => swapFrame(frame, index), index * 170));
    }
    if (!window.matchMedia('(prefers-reduced-motion: reduce)').matches) {
        setInterval(rotate, 4800);
    }
}
initHeroCollage();

// Ensure cinematic background videos autoplay reliably across modern browsers
function initBackgroundVideos() {
    const videos = document.querySelectorAll('.global-bg-video');
    videos.forEach(video => {
        video.muted = true;
        video.defaultMuted = true;
        video.playsInline = true;
        video.setAttribute('muted', '');
        video.setAttribute('playsinline', '');

        const startPlayback = () => {
            if (video.paused) {
                const p = video.play();
                if (p && p.catch) {
                    p.catch(() => {
                        // User gesture needed; hook to the very first user interaction
                        const onInteract = () => {
                            video.play().catch(() => {});
                            ['click', 'touchstart', 'keydown', 'scroll', 'mousemove'].forEach(evt =>
                                window.removeEventListener(evt, onInteract)
                            );
                        };
                        ['click', 'touchstart', 'keydown', 'scroll', 'mousemove'].forEach(evt =>
                            window.addEventListener(evt, onInteract, { once: true, passive: true })
                        );
                    });
                }
            }
        };

        if (video.readyState >= 2) {
            startPlayback();
        } else {
            video.addEventListener('canplay', startPlayback, { once: true });
            video.addEventListener('loadedmetadata', startPlayback, { once: true });
            try { video.load(); } catch (e) {}
        }
    });
}

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initBackgroundVideos);
} else {
    initBackgroundVideos();
}
