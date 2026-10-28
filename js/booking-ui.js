(function(window){
    async function requireUser() {
        try { return (await SerendibApi.auth.me()).data; }
        catch (e) {
            const here = encodeURIComponent(location.pathname.split('/').pop() + location.search + location.hash);
            location.href = `login.html?reason=booking-login-required&return=${here}`;
            throw e;
        }
    }

    async function createAndPay(payload, paymentMethod, slipFile, statusEl) {
        await requireUser();
        // Validate payment input before creating a booking so an accidental
        // missing receipt does not create duplicate unpaid booking records.
        if (paymentMethod === 'SLIP' && !slipFile) {
            throw new Error('Please select your payment receipt/slip image or PDF.');
        }
        if (!['SLIP','CARD_DEMO'].includes(paymentMethod)) {
            throw new Error('Please choose a valid payment method.');
        }
        if (statusEl) { statusEl.className='notice'; statusEl.textContent='Creating booking...'; }
        const created = await SerendibApi.bookings.create(payload);
        let booking = created.data;
        if (paymentMethod === 'SLIP') {
            if (statusEl) statusEl.textContent='Uploading payment receipt...';
            booking = (await SerendibApi.bookings.uploadSlip(booking.id, slipFile)).data;
        } else if (paymentMethod === 'CARD_DEMO') {
            if (statusEl) statusEl.textContent='Recording secure demo card payment...';
            booking = (await SerendibApi.bookings.cardDemo(booking.id)).data;
        }
        if (statusEl) {
            statusEl.className='notice success';
            statusEl.innerHTML=`Booking #${booking.id} submitted. Current status: <strong>${booking.status}</strong>. You can follow it in <a href="bookings.html">My Bookings</a>.`;
        }
        return booking;
    }

    function paymentFields(prefix='pay') {
        return `<div class="payment-box">
            <h4>Payment method</h4>
            <label class="payment-choice"><input type="radio" name="${prefix}Method" value="SLIP" checked> <span><strong>Bank transfer / payment slip</strong><small>Upload an image or PDF receipt. Admin reviews it before confirmation.</small></span></label>
            <label class="payment-choice"><input type="radio" name="${prefix}Method" value="CARD_DEMO"> <span><strong>Online card — demo gateway</strong><small>Works end-to-end for this local project without storing card numbers. No real money is charged.</small></span></label>
            <div class="field" id="${prefix}SlipWrap"><label>PAYMENT RECEIPT / SLIP</label><input id="${prefix}Slip" type="file" accept="image/*,.pdf"></div>
        </div>`;
    }

    function wirePaymentToggle(prefix='pay') {
        const radios=document.querySelectorAll(`input[name="${prefix}Method"]`);
        const wrap=document.getElementById(`${prefix}SlipWrap`);
        const update=()=>{ const selected=document.querySelector(`input[name="${prefix}Method"]:checked`)?.value; if(wrap)wrap.style.display=selected==='SLIP'?'block':'none'; };
        radios.forEach(r=>r.addEventListener('change',update)); update();
    }

    window.SerendibBookingUI={requireUser,createAndPay,paymentFields,wirePaymentToggle};
})(window);
