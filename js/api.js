/** Serendib API client - all browser/server traffic goes through this file. */
(function (window) {
    const API_BASE = window.location.protocol === 'file:' ? 'http://localhost:8080/api' : '/api';

    async function request(endpoint, options = {}) {
        const opts = { credentials: 'same-origin', cache: 'no-store', ...options };
        const isForm = opts.body instanceof FormData;
        opts.headers = { ...(isForm ? {} : { 'Content-Type': 'application/json' }), ...(opts.headers || {}) };
        const res = await fetch(`${API_BASE}${endpoint}`, opts);
        const type = res.headers.get('content-type') || '';
        const data = type.includes('application/json') ? await res.json().catch(() => ({})) : { success: res.ok, data: await res.text() };
        if (!res.ok) {
            const err = new Error(data.message || `Request failed with status ${res.status}`);
            err.status = res.status;
            err.data = data;
            throw err;
        }
        return data;
    }

    const crud = (base) => ({
        getAll: () => request(base),
        getById: (id) => request(`${base}/${id}`),
        create: (data) => request(base, { method: 'POST', body: JSON.stringify(data) }),
        update: (id, data) => request(`${base}/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
        delete: (id) => request(`${base}/${id}`, { method: 'DELETE' })
    });

    const Api = {
        baseUrl: API_BASE,
        destinations: {
            ...crud('/destinations'),
            getAll: (region) => request(`/destinations${region ? `?region=${encodeURIComponent(region)}` : ''}`)
        },
        transport: {
            ...crud('/transport'),
            getAll: (type) => request(`/transport${type ? `?type=${encodeURIComponent(type)}` : ''}`)
        },
        hotels: {
            ...crud('/hotels'),
            getAll: (destination) => request(`/hotels${destination ? `?destination=${encodeURIComponent(destination)}` : ''}`)
        },
        packages: {
            ...crud('/packages'),
            getFeatured: () => request('/packages/featured'),
            getAll: (params = {}) => {
                const q = new URLSearchParams();
                Object.entries(params).forEach(([k, v]) => { if (v !== undefined && v !== null && v !== '') q.append(k, v); });
                return request(`/packages${q.toString() ? `?${q}` : ''}`);
            }
        },
        users: crud('/users'),
        auth: {
            login: (email, password) => request('/auth/login', { method: 'POST', body: JSON.stringify({ email, password }) }),
            register: (data) => request('/auth/register', { method: 'POST', body: JSON.stringify(data) }),
            me: () => request('/auth/me'),
            updateProfile: (data) => request('/auth/profile', { method: 'PUT', body: JSON.stringify(data) }),
            changePassword: (_email, currentPassword, newPassword) => request('/auth/change-password', { method: 'POST', body: JSON.stringify({ email: _email || 'session-user', currentPassword, newPassword }) }),
            logout: () => request('/auth/logout', { method: 'POST' })
        },
        bookings: {
            my: () => request('/bookings/me'),
            create: (data) => request('/bookings', { method: 'POST', body: JSON.stringify(data) }),
            uploadSlip: (id, file) => {
                const fd = new FormData(); fd.append('file', file);
                return request(`/bookings/${id}/slip`, { method: 'POST', body: fd });
            },
            cardDemo: (id) => request(`/bookings/${id}/card-demo`, { method: 'POST' }),
            cancel: (id) => request(`/bookings/${id}/cancel`, { method: 'POST' })
        },
        admin: {
            getStats: () => request('/admin/stats'),
            getBookings: () => request('/admin/bookings'),
            updateBookingStatus: (id, data) => request(`/admin/bookings/${id}/status`, { method: 'PUT', body: JSON.stringify(data) }),
            deleteBooking: (id) => request(`/admin/bookings/${id}`, { method: 'DELETE' }),
            uploadImage: (file) => {
                const fd = new FormData(); fd.append('file', file);
                return request('/admin/uploads', { method: 'POST', body: fd });
            }
        },
        home: { getData: () => request('/home') },
        async syncData() {
            try {
                const [pkgs, dests, vehicles, hotels] = await Promise.all([
                    this.packages.getAll(), this.destinations.getAll(), this.transport.getAll(), this.hotels.getAll()
                ]);
                if (pkgs.data) window.CEYLON_DATA.packages = pkgs.data;
                if (dests.data) window.CEYLON_DATA.destinations = dests.data;
                if (vehicles.data) window.CEYLON_DATA.vehicles = vehicles.data;
                if (hotels.data) window.CEYLON_DATA.hotels = hotels.data;
                return true;
            } catch (e) { return false; }
        }
    };
    window.SerendibApi = Api;
})(window);
