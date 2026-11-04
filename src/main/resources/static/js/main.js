/**
 * Tamil Nadu Smart Hospital - Main Interactive JavaScript
 * Responsive Navigation, Search Enhancements & Client Helpers
 */

document.addEventListener('DOMContentLoaded', function () {
    // 1. Mobile Navigation Toggle
    const navToggle = document.querySelector('.mobile-nav-toggle');
    const navLinks = document.querySelector('.nav-links');

    if (navToggle && navLinks) {
        navToggle.addEventListener('click', function () {
            navLinks.classList.toggle('open');
            const isExpanded = navLinks.classList.contains('open');
            navToggle.setAttribute('aria-expanded', isExpanded);
        });

        // Close menu when clicking outside
        document.addEventListener('click', function (e) {
            if (!navToggle.contains(e.target) && !navLinks.contains(e.target)) {
                navLinks.classList.remove('open');
                navToggle.setAttribute('aria-expanded', 'false');
            }
        });
    }

    // 2. Phone input restriction to 10 numerical digits
    const phoneInputs = document.querySelectorAll('input[type="tel"], input[name="patientPhone"], input[name="phone"]');
    phoneInputs.forEach(input => {
        input.addEventListener('input', function () {
            this.value = this.value.replace(/\D/g, '').slice(0, 10);
        });
    });

    // 3. Print Slip Helper
    const printBtn = document.getElementById('printSlipBtn');
    if (printBtn) {
        printBtn.addEventListener('click', function () {
            window.print();
        });
    }

    // 4. Copy Booking Reference Helper
    const copyRefBtn = document.getElementById('copyRefBtn');
    if (copyRefBtn) {
        copyRefBtn.addEventListener('click', function () {
            const refText = document.getElementById('bookingRefText');
            if (refText) {
                navigator.clipboard.writeText(refText.textContent.trim()).then(() => {
                    const originalText = copyRefBtn.innerHTML;
                    copyRefBtn.innerHTML = '✓ Copied!';
                    setTimeout(() => {
                        copyRefBtn.innerHTML = originalText;
                    }, 2000);
                });
            }
        });
    }

    // 5. Cancel Confirmation Modal/Dialog
    const cancelForms = document.querySelectorAll('.confirm-cancel-form');
    cancelForms.forEach(form => {
        form.addEventListener('submit', function (e) {
            const confirmed = window.confirm("Are you sure you want to cancel this demo appointment? The slot will be released back to other patients.");
            if (!confirmed) {
                e.preventDefault();
            }
        });
    });

    // 6. District & City Auto-fill helper (Sample Tamil Nadu mappings)
    const districtSelect = document.getElementById('districtSelect');
    const cityInput = document.getElementById('cityInput');
    const sampleCitiesByDistrict = {
        'Chennai': ['Guindy', 'Adyar', 'Anna Nagar', 'Tambaram', 'T. Nagar', 'Mylapore'],
        'Coimbatore': ['Peelamedu', 'RS Puram', 'Gandhipuram', 'Saravanampatti', 'Singanallur'],
        'Madurai': ['KK Nagar', 'Goripalayam', 'Anna Nagar', 'Tallakulam'],
        'Tiruchirappalli': ['Thillai Nagar', 'Cantonment', 'Srirangam', 'K.K. Nagar'],
        'Salem': ['Fairlands', 'Hasthampatti', 'Suramangalam', 'Shevapet'],
        'Tirunelveli': ['Palayamkottai', 'Vannarpettai', 'Town'],
        'Vellore': ['Katpadi', 'Sathuvachari', 'Bagayam'],
        'Thoothukudi': ['Palayamkottai Road', 'Millerpuram', 'Muthiahpuram'],
        'Thanjavur': ['Medical College Road', 'Old Bus Stand', 'Shivaji Nagar'],
        'Nilgiris': ['Udhagamandalam', 'Coonoor', 'Kotagiri']
    };

    if (districtSelect && cityInput) {
        districtSelect.addEventListener('change', function () {
            const selected = this.value;
            if (sampleCitiesByDistrict[selected] && (!cityInput.value || cityInput.value.trim() === '')) {
                cityInput.setAttribute('placeholder', 'e.g. ' + sampleCitiesByDistrict[selected][0]);
            }
        });
    }

    // 7. Smooth Scroll for Homepage In-Page Anchor Links
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            const targetId = this.getAttribute('href');
            if (targetId && targetId !== '#') {
                const targetElement = document.querySelector(targetId);
                if (targetElement) {
                    e.preventDefault();
                    targetElement.scrollIntoView({ behavior: 'smooth', block: 'start' });
                }
            }
        });
    });
});

