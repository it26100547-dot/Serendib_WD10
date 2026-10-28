// Serendib Admin - schema-driven full CRUD editor.
const ADMIN_API = '/api';
const type = document.body.dataset.adminType || '';
let rows = [];
let backendConnected = true;

async function api(endpoint, options = {}) {
    const opts = { credentials: 'same-origin', cache: 'no-store', ...options };
    const isForm = opts.body instanceof FormData;
    opts.headers = { ...(isForm ? {} : {'Content-Type':'application/json'}), ...(opts.headers || {}) };
    const res = await fetch(`${ADMIN_API}${endpoint}`, opts);
    if (res.status === 401) { location.replace('../login.html?reason=admin-login-required'); throw new Error('Admin login required'); }
    if (res.status === 403) { location.replace('../profile.html?reason=admin-only'); throw new Error('Admin permission required'); }
    const json = await res.json().catch(() => ({}));
    if (!res.ok) throw new Error(json.message || `Request failed (${res.status})`);
    return json;
}

function esc(v) { return String(v ?? '').replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c])); }
function lines(v) { return Array.isArray(v) ? v.join('\n') : (v || ''); }
function csv(v) { return Array.isArray(v) ? v.join(', ') : (v || ''); }
function splitLines(v) { return String(v || '').split(/\r?\n/).map(x=>x.trim()).filter(Boolean); }
function splitCsv(v) { return String(v || '').split(',').map(x=>x.trim()).filter(Boolean); }
function splitNumCsv(v) { return splitCsv(v).map(Number).filter(Number.isFinite); }

async function adminLogout() {
    try {
        await fetch('/api/auth/logout', { method:'POST', credentials:'same-origin', cache:'no-store' });
    } catch (e) {
        // The local browser state is still cleared below.
    }
    localStorage.removeItem('serendib_user');
    location.replace('../login.html?loggedOut=1');
}

function installAdminLogout() {
    const nav = document.querySelector('.sidebar-nav');
    if (!nav || document.getElementById('adminLogoutLink')) return;
    const link = document.createElement('a');
    link.id = 'adminLogoutLink';
    link.href = '#';
    link.textContent = 'Logout';
    link.addEventListener('click', (event) => {
        event.preventDefault();
        adminLogout();
    });
    nav.appendChild(link);
}

installAdminLogout();

const common = [
    {key:'name', label:'Name / Title', type:'text', required:true},
    {key:'category', label:'Category', type:'text'},
    {key:'summary', label:'Card summary', type:'textarea', full:true},
    {key:'description', label:'Full description', type:'textarea', full:true},
    {key:'featured', label:'Show as featured', type:'checkbox'},
    {key:'active', label:'Active', type:'checkbox'}
];

const schemas = {
    packages: [
        ...common,
        {key:'price', label:'Price', type:'number', step:'0.01'},
        {key:'normalPrice', label:'Normal / old price', type:'number', step:'0.01'},
        {key:'priceUnit', label:'Price label', type:'text', placeholder:'per person'},
        {key:'days', label:'Days', type:'number'}, {key:'nights', label:'Nights', type:'number'},
        {key:'rating', label:'Rating', type:'number', step:'0.1'},
        {key:'cover', label:'Card / cover image URL', type:'text', full:true},
        {key:'coverFile', label:'Upload card / cover image', type:'file', accept:'image/*', full:true, uploadTo:'cover'},
        {key:'images', label:'Gallery image URLs (one per line)', type:'lines', full:true},
        {key:'galleryFiles', label:'Upload gallery images', type:'file', accept:'image/*', multiple:true, full:true, appendTo:'images'},
        {key:'route', label:'Package route', type:'textarea', full:true, placeholder:'Colombo → Kandy → Nuwara Eliya → Ella'},
        {key:'destinations', label:'Destination names (comma separated)', type:'csv', full:true},
        {key:'destinationIds', label:'Destination IDs (comma separated)', type:'numcsv'},
        {key:'transportId', label:'Included transport ID', type:'number'},
        {key:'hotelId', label:'Recommended hotel ID', type:'number'},
        {key:'itinerary', label:'Day-by-day itinerary — one line: Title | Description', type:'itinerary', full:true},
        {key:'highlights', label:'Highlights (one per line)', type:'lines', full:true},
        {key:'inclusions', label:'Included (one per line)', type:'lines', full:true},
        {key:'exclusions', label:'Not included (one per line)', type:'lines', full:true},
        {key:'meetingPoint', label:'Meeting / pickup point', type:'text', full:true},
        {key:'cancellationPolicy', label:'Cancellation policy', type:'textarea', full:true},
        {key:'bookingNote', label:'Booking note', type:'textarea', full:true}
    ],
    destinations: [
        ...common,
        {key:'region', label:'Region', type:'text'},
        {key:'bestTime', label:'Best time to visit', type:'text'},
        {key:'suggestedDays', label:'Suggested days', type:'number'},
        {key:'image', label:'Card / main image URL', type:'text', full:true},
        {key:'imageFile', label:'Upload card / main image', type:'file', accept:'image/*', full:true, uploadTo:'image'},
        {key:'images', label:'Gallery image URLs (one per line)', type:'lines', full:true},
        {key:'galleryFiles', label:'Upload gallery images', type:'file', accept:'image/*', multiple:true, full:true, appendTo:'images'},
        {key:'attractions', label:'Simple attraction names (one per line)', type:'lines', full:true},
        {key:'places', label:'Places to explore — Name | Description | Image URL | Category', type:'places', full:true},
        {key:'recommendedHotelIds', label:'Recommended hotel IDs (comma separated)', type:'numcsv'},
        {key:'recommendedTransportIds', label:'Recommended vehicle IDs (comma separated)', type:'numcsv'},
        {key:'travelTips', label:'Travel tips', type:'textarea', full:true}
    ],
    hotels: [
        ...common,
        {key:'destination', label:'Destination / city', type:'text'},
        {key:'rating', label:'Rating', type:'number', step:'0.1'}, {key:'stars', label:'Stars (1–5)', type:'number'},
        {key:'fromPrice', label:'Starting price / night', type:'number', step:'0.01'},
        {key:'image', label:'Card / main image URL', type:'text', full:true},
        {key:'imageFile', label:'Upload card / main image', type:'file', accept:'image/*', full:true, uploadTo:'image'},
        {key:'images', label:'Gallery image URLs (one per line)', type:'lines', full:true},
        {key:'galleryFiles', label:'Upload gallery images', type:'file', accept:'image/*', multiple:true, full:true, appendTo:'images'},
        {key:'address', label:'Address', type:'text', full:true},
        {key:'contactPhone', label:'Contact phone', type:'text'},
        {key:'checkIn', label:'Check-in time', type:'text'}, {key:'checkOut', label:'Check-out time', type:'text'},
        {key:'amenities', label:'Amenities (one per line)', type:'lines', full:true},
        {key:'rooms', label:'Rooms — Name | Capacity | Price | Description | Image URL', type:'rooms', full:true},
        {key:'cancellationPolicy', label:'Cancellation policy', type:'textarea', full:true}
    ],
    vehicles: [
        ...common.filter(f=>f.key!=='category'),
        {key:'type', label:'Vehicle type', type:'text'}, {key:'capacity', label:'Passenger capacity', type:'number'},
        {key:'price', label:'Price / day', type:'number', step:'0.01'},
        {key:'status', label:'Availability', type:'select', options:['Available','Booked','Maintenance']},
        {key:'driver', label:'Driver available', type:'checkbox'}, {key:'ac', label:'Air conditioning', type:'checkbox'},
        {key:'image', label:'Card / main image URL', type:'text', full:true},
        {key:'imageFile', label:'Upload card / main image', type:'file', accept:'image/*', full:true, uploadTo:'image'},
        {key:'images', label:'Gallery image URLs (one per line)', type:'lines', full:true},
        {key:'galleryFiles', label:'Upload gallery images', type:'file', accept:'image/*', multiple:true, full:true, appendTo:'images'},
        {key:'features', label:'Features (one per line)', type:'lines', full:true},
        {key:'pickupAreas', label:'Pickup areas (one per line)', type:'lines', full:true},
        {key:'terms', label:'Rental / booking terms', type:'textarea', full:true}
    ],
    users: [
        {key:'firstName', label:'First name', type:'text', required:true}, {key:'lastName', label:'Last name', type:'text'},
        {key:'email', label:'Email', type:'email', required:true, full:true}, {key:'phone', label:'Phone', type:'text'},
        {key:'country', label:'Country', type:'text'}, {key:'role', label:'Role', type:'select', options:['TRAVELER','ADMIN']},
        {key:'password', label:'Password (leave blank on edit to keep existing)', type:'password', full:true},
        {key:'active', label:'Active account', type:'checkbox'}
    ]
};

const endpoints = {packages:'/packages', destinations:'/destinations', hotels:'/hotels', vehicles:'/transport', users:'/users'};

function fieldValue(field, row) {
    const v = row?.[field.key];
    if (field.type === 'lines') return lines(v);
    if (field.type === 'csv' || field.type === 'numcsv') return csv(v);
    if (field.type === 'itinerary') return Array.isArray(v) ? v.map(x => Array.isArray(x) ? `${x[0]||''} | ${x[1]||''}` : '').filter(Boolean).join('\n') : '';
    if (field.type === 'places') return Array.isArray(v) ? v.map(x => `${x.name||''} | ${x.description||''} | ${x.image||''} | ${x.category||''}`).join('\n') : '';
    if (field.type === 'rooms') return Array.isArray(v) ? v.map(x => Array.isArray(x) ? `${x[0]||''} | ${x[1]||2} | ${x[2]||0}` : `${x.name||''} | ${x.capacity||2} | ${x.price||0} | ${x.description||''} | ${x.image||''}`).join('\n') : '';
    return v ?? '';
}

function fieldHtml(field, row) {
    const cls = `field${field.full ? ' full' : ''}`;
    const id = `f_${field.key}`;
    if (field.type === 'checkbox') {
        const checked = row ? (row[field.key] !== false) : (field.key === 'active' || field.key === 'driver' || field.key === 'ac');
        return `<div class="${cls} admin-check"><label><input id="${id}" type="checkbox" ${checked?'checked':''}> ${esc(field.label)}</label></div>`;
    }
    if (field.type === 'select') {
        return `<div class="${cls}"><label>${esc(field.label)}</label><select id="${id}">${field.options.map(o=>`<option ${String(fieldValue(field,row)).toLowerCase()===String(o).toLowerCase()?'selected':''}>${esc(o)}</option>`).join('')}</select></div>`;
    }
    if (field.type === 'textarea' || ['lines','csv','numcsv','itinerary','places','rooms'].includes(field.type)) {
        return `<div class="${cls}"><label>${esc(field.label)}</label><textarea id="${id}" placeholder="${esc(field.placeholder||'')}">${esc(fieldValue(field,row))}</textarea></div>`;
    }
    if (field.type === 'file') {
        return `<div class="${cls}"><label>${esc(field.label)}</label><input id="${id}" type="file" ${field.accept?`accept="${field.accept}"`:''} ${field.multiple?'multiple':''}><small class="field-help">Uploaded files are saved by the backend and the URL is stored automatically.</small></div>`;
    }
    return `<div class="${cls}"><label>${esc(field.label)}</label><input id="${id}" type="${field.type||'text'}" value="${field.type==='password'?'':esc(fieldValue(field,row))}" ${field.step?`step="${field.step}"`:''} ${field.required?'required':''} placeholder="${esc(field.placeholder||'')}"></div>`;
}

function buildForm(row = null, index = null) {
    const form = document.getElementById('crudForm');
    if (!form || !schemas[type]) return;
    form.innerHTML = `<input type="hidden" id="editIndex" value="${index ?? ''}">` + schemas[type].map(f=>fieldHtml(f,row)).join('') + `<div class="full admin-modal-actions"><button class="btn btn-primary" type="submit">Save changes</button><button class="btn btn-dark" type="button" onclick="closeModal()">Cancel</button></div>`;
}

function openModal(index = null) {
    const modal = document.getElementById('crudModal'); if (!modal) return;
    const row = index == null ? null : rows[index];
    buildForm(row, index);
    const h = modal.querySelector('.modal-head h2'); if (h) h.textContent = index == null ? 'Add New' : `Edit ${row?.name || row?.title || 'Item'}`;
    modal.classList.add('open');
    modal.querySelector('input:not([type=hidden]):not([type=file]), textarea')?.focus();
}
function closeModal(){ document.getElementById('crudModal')?.classList.remove('open'); }
function editRow(i){ openModal(i); }

async function uploadOne(file) {
    const fd = new FormData(); fd.append('file', file);
    const r = await api('/admin/uploads', {method:'POST', body:fd});
    return r.data.url;
}

async function formPayload(existing = {}) {
    const payload = {...existing};
    for (const f of schemas[type]) {
        const el = document.getElementById(`f_${f.key}`); if (!el) continue;
        if (f.type === 'file') continue;
        let v;
        if (f.type === 'checkbox') v = el.checked;
        else if (f.type === 'number') v = el.value === '' ? null : Number(el.value);
        else if (f.type === 'lines') v = splitLines(el.value);
        else if (f.type === 'csv') v = splitCsv(el.value);
        else if (f.type === 'numcsv') v = splitNumCsv(el.value);
        else if (f.type === 'itinerary') v = splitLines(el.value).map(line=>{ const [a,...rest]=line.split('|'); return [a.trim(),rest.join('|').trim()]; }).filter(x=>x[0]);
        else if (f.type === 'places') v = splitLines(el.value).map(line=>{ const [name,description,image,category] = line.split('|').map(x=>(x||'').trim()); return {name,description,image,category}; }).filter(x=>x.name);
        else if (f.type === 'rooms') v = splitLines(el.value).map(line=>{ const [name,capacity,price,description,image] = line.split('|').map(x=>(x||'').trim()); return {name,capacity:Number(capacity)||2,price:Number(price)||0,description,image}; }).filter(x=>x.name);
        else v = el.value.trim();
        if (f.type === 'password' && !v) continue;
        payload[f.key] = v;
    }
    // Upload primary/gallery files after ordinary values are collected.
    for (const f of schemas[type].filter(x=>x.type==='file')) {
        const input = document.getElementById(`f_${f.key}`); if (!input?.files?.length) continue;
        if (f.uploadTo) payload[f.uploadTo] = await uploadOne(input.files[0]);
        if (f.appendTo) {
            const uploaded = [];
            for (const file of input.files) uploaded.push(await uploadOne(file));
            payload[f.appendTo] = [...(Array.isArray(payload[f.appendTo]) ? payload[f.appendTo] : []), ...uploaded];
        }
    }
    if (type === 'vehicles') payload.category = payload.type;
    if (type === 'hotels') payload.category = payload.category || 'Hotel';
    if (type === 'users' && !existing.id && !payload.password) payload.password = 'password123';
    return payload;
}

function statusPill(active, text) { return `<span class="status ${active?'active':'pending'}">${esc(text)}</span>`; }

function render() {
    const table = document.getElementById('adminTable'); if (!table) return;
    if (type === 'bookings') { renderBookings(); return; }
    const tbody = table.querySelector('tbody'); if (!tbody) return;
    if (!rows.length) { tbody.innerHTML='<tr><td colspan="6" style="text-align:center;padding:28px">No records found.</td></tr>'; return; }
    tbody.innerHTML = rows.map((r,i)=>{
        const name = r.name || r.title || [r.firstName,r.lastName].filter(Boolean).join(' ') || 'Untitled';
        const cat = r.category || r.type || r.destination || r.role || '-';
        const value = r.price != null ? `$${Number(r.price).toFixed(0)}` : r.fromPrice != null ? `From $${Number(r.fromPrice).toFixed(0)}` : r.email || '-';
        return `<tr><td>#${esc(r.id)}</td><td><strong>${esc(name)}</strong>${r.image||r.cover||r.thumbnail?`<br><img class="admin-thumb" src="${esc(r.image||r.cover||r.thumbnail)}" alt="">`:''}</td><td>${esc(cat)}</td><td>${esc(value)}</td><td>${statusPill(r.active!==false,r.active!==false?'Active':'Inactive')}</td><td><div class="action-row"><button class="edit" onclick="editRow(${i})">Edit all details</button><button class="delete" onclick="deleteRow(${i})">Delete</button></div></td></tr>`;
    }).join('');
}

async function fetchRows() {
    if (!endpoints[type]) return;
    try { const r = await api(endpoints[type]); rows = Array.isArray(r.data)?r.data:[]; backendConnected=true; render(); }
    catch(e){ backendConnected=false; const tbody=document.querySelector('#adminTable tbody'); if(tbody) tbody.innerHTML=`<tr><td colspan="6">${esc(e.message)}</td></tr>`; }
}

async function deleteRow(i) {
    const r=rows[i]; if(!r || !confirm(`Delete "${r.name||r.title||r.email||'this item'}"?`)) return;
    try { await api(`${endpoints[type]}/${r.id}`, {method:'DELETE'}); await fetchRows(); } catch(e){ alert(e.message); }
}

function renderBookings() {
    const tbody=document.querySelector('#adminTable tbody'); if(!tbody) return;
    if(!rows.length){tbody.innerHTML='<tr><td colspan="9" style="text-align:center;padding:28px">No bookings yet.</td></tr>';return;}
    tbody.innerHTML=rows.map(b=>`<tr>
        <td>#${esc(b.id)}</td><td><strong>${esc(b.customerName||b.userEmail)}</strong><br><small>${esc(b.userEmail||'')}</small></td>
        <td>${esc(b.bookingType)}</td><td>${esc(b.itemName)}${b.optionName?`<br><small>${esc(b.optionName)}</small>`:''}${b.customerNote?`<br><small><strong>Request:</strong> ${esc(b.customerNote)}</small>`:''}</td>
        <td>${esc(b.startDate||'-')} → ${esc(b.endDate||'-')}</td><td>$${Number(b.total||0).toFixed(2)}</td>
        <td>${esc(b.paymentMethod)}<br>${statusPill(['SUBMITTED','PAID_DEMO','APPROVED'].includes(b.paymentStatus),b.paymentStatus)}${b.transactionReference?`<br><small>${esc(b.transactionReference)}</small>`:''}${b.paymentSlipUrl?`<br><a class="admin-file-link" href="${esc(b.paymentSlipUrl)}" target="_blank">View receipt</a>`:''}</td>
        <td>${statusPill(b.status==='CONFIRMED',b.status)}${b.adminNote?`<br><small>${esc(b.adminNote)}</small>`:''}</td>
        <td><select class="booking-status-select" id="bookingStatus_${b.id}"><option>PENDING_PAYMENT</option><option>PAYMENT_SUBMITTED</option><option>CONFIRMED</option><option>REJECTED</option><option>CANCELLED</option></select><textarea class="booking-admin-note" id="bookingNote_${b.id}" placeholder="Admin note / rejection reason">${esc(b.adminNote||'')}</textarea><button class="edit" style="margin-top:6px" onclick="saveBookingStatus(${b.id})">Update</button></td>
    </tr>`).join('');
    rows.forEach(b=>{ const s=document.getElementById(`bookingStatus_${b.id}`); if(s)s.value=b.status; });
}
async function fetchBookings(){ const r=await api('/admin/bookings'); rows=r.data||[]; render(); }
async function saveBookingStatus(id){
    const status=document.getElementById(`bookingStatus_${id}`)?.value; if(!status)return;
    const adminNote=document.getElementById(`bookingNote_${id}`)?.value?.trim() || '';
    try{await api(`/admin/bookings/${id}/status`,{method:'PUT',body:JSON.stringify({status,adminNote})});await fetchBookings();}catch(e){alert(e.message);}
}

async function initDashboardStats() {
    if (!location.pathname.endsWith('/dashboard.html') && !location.pathname.endsWith('/admin/')) return;
    try {
        const [statsRes, bookingsRes] = await Promise.all([api('/admin/stats'), api('/admin/bookings')]);
        const st=statsRes.data||{};
        document.querySelectorAll('.stat').forEach(box=>{
            const label=box.querySelector('span')?.textContent?.trim().toUpperCase(); const value=box.querySelector('strong'); if(!label||!value)return;
            if(label.includes('PACKAGE'))value.textContent=st.totalPackages??0;
            else if(label.includes('DESTINATION'))value.textContent=st.totalDestinations??0;
            else if(label.includes('HOTEL'))value.textContent=st.totalHotels??0;
            else if(label.includes('VEHICLE')||label.includes('TRANSPORT'))value.textContent=st.totalTransports??0;
            else if(label.includes('USER'))value.textContent=st.totalUsers??0;
            else if(label.includes('BOOKING'))value.textContent=st.totalBookings??0;
            else if(label.includes('PENDING'))value.textContent=(bookingsRes.data||[]).filter(b=>b.status!=='CONFIRMED'&&b.status!=='CANCELLED'&&b.status!=='REJECTED').length;
        });
        const tbody=document.querySelector('.table-card tbody');
        if(tbody){ tbody.innerHTML=(bookingsRes.data||[]).slice(-5).reverse().map(b=>`<tr><td>#${esc(b.id)}</td><td>${esc(b.customerName||b.userEmail)}</td><td>${esc(b.bookingType)}</td><td>${esc(b.itemName)}</td><td>${statusPill(b.status==='CONFIRMED',b.status)}</td></tr>`).join('') || '<tr><td colspan="5">No bookings yet.</td></tr>'; }
    } catch(e){ console.error(e); }
}

document.getElementById('crudForm')?.addEventListener('submit', async e=>{
    e.preventDefault();
    const idx=document.getElementById('editIndex')?.value; const isEdit=idx!==''&&idx!=null; const existing=isEdit?rows[Number(idx)]:{};
    try{
        const payload=await formPayload(existing);
        const url=isEdit?`${endpoints[type]}/${existing.id}`:endpoints[type];
        await api(url,{method:isEdit?'PUT':'POST',body:JSON.stringify(payload)});
        closeModal(); await fetchRows();
    }catch(err){alert(`Save failed: ${err.message}`);}
});

document.getElementById('crudModal')?.addEventListener('click',e=>{if(e.target.id==='crudModal')closeModal();});
document.addEventListener('keydown',e=>{if(e.key==='Escape')closeModal();});

if(type==='bookings') fetchBookings();
else if(type) fetchRows();
else initDashboardStats();
