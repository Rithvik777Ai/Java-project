/**
 * CuraTransit - Smart Hospital Finder & Medical Transit JavaScript
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
    }

    // 2. Phone input numeric constraint
    const phoneInputs = document.querySelectorAll('input[type="tel"], input[name="patientPhone"], input[name="phone"]');
    phoneInputs.forEach(input => {
        input.addEventListener('input', function (e) {
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

    // 5. Cancel Confirmation Dialog
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

    // 7. Interactive Floating Compare Bar
    const compareCheckboxes = document.querySelectorAll('.compare-checkbox');
    const floatingBar = document.getElementById('floatingCompareBar');
    const compareCountText = document.getElementById('compareCountText');
    const compareGoBtn = document.getElementById('compareGoBtn');
    const compareClearBtn = document.getElementById('compareClearBtn');

    if (compareCheckboxes.length > 0 && floatingBar) {
        function updateCompareBar() {
            const checkedBoxes = Array.from(compareCheckboxes).filter(cb => cb.checked);
            const count = checkedBoxes.length;

            if (count > 0) {
                floatingBar.style.display = 'flex';
                if (count === 1) {
                    compareCountText.textContent = '1 hospital selected (select at least 2)';
                    compareGoBtn.disabled = true;
                    compareGoBtn.style.opacity = '0.6';
                    compareGoBtn.style.cursor = 'not-allowed';
                } else if (count > 4) {
                    compareCountText.textContent = count + ' hospitals (max 4 allowed)';
                    compareGoBtn.disabled = true;
                    compareGoBtn.style.opacity = '0.6';
                    compareGoBtn.style.cursor = 'not-allowed';
                } else {
                    compareCountText.textContent = count + ' hospitals selected';
                    compareGoBtn.disabled = false;
                    compareGoBtn.style.opacity = '1';
                    compareGoBtn.style.cursor = 'pointer';
                }
            } else {
                floatingBar.style.display = 'none';
            }
        }

        compareCheckboxes.forEach(cb => {
            cb.addEventListener('change', updateCompareBar);
        });

        if (compareGoBtn) {
            compareGoBtn.addEventListener('click', function () {
                const checkedBoxes = Array.from(compareCheckboxes).filter(cb => cb.checked);
                const ids = checkedBoxes.map(cb => cb.value).join(',');
                if (ids) {
                    window.location.href = '/hospitals/compare?ids=' + encodeURIComponent(ids);
                }
            });
        }

        if (compareClearBtn) {
            compareClearBtn.addEventListener('click', function () {
                compareCheckboxes.forEach(cb => { cb.checked = false; });
                floatingBar.style.display = 'none';
            });
        }
    }

    // 8. Outstation Travel & Accessibility Dynamic Form Toggles
    const outstationCheckbox = document.getElementById('outstationCheckbox');
    const originGroup = document.getElementById('originGroup');
    if (outstationCheckbox && originGroup) {
        outstationCheckbox.addEventListener('change', function () {
            originGroup.style.display = this.checked ? 'block' : 'none';
            if (this.checked) {
                const input = originGroup.querySelector('input');
                if (input) input.focus();
            }
        });
    }

    const languageCheckbox = document.getElementById('languageCheckbox');
    const languageGroup = document.getElementById('languageGroup');
    if (languageCheckbox && languageGroup) {
        languageCheckbox.addEventListener('change', function () {
            languageGroup.style.display = this.checked ? 'block' : 'none';
        });
    }
});
