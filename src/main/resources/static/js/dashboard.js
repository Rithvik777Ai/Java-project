/**
 * Tamil Nadu Smart Hospital - Dashboard Interactive JavaScript
 * Sidebar, Tab Navigation, and Action Handlers
 */

document.addEventListener('DOMContentLoaded', function () {
    // 1. Mobile Sidebar Toggle
    const sidebarToggle = document.querySelector('.dash-sidebar-toggle');
    const sidebar = document.querySelector('.dash-sidebar');

    if (sidebarToggle && sidebar) {
        sidebarToggle.addEventListener('click', function () {
            sidebar.classList.toggle('open');
        });

        // Close when clicking outside on mobile
        document.addEventListener('click', function (e) {
            if (window.innerWidth <= 992) {
                if (!sidebar.contains(e.target) && !sidebarToggle.contains(e.target)) {
                    sidebar.classList.remove('open');
                }
            }
        });
    }

    // 2. Tab Navigation within Dashboards
    const navLinks = document.querySelectorAll('.dash-nav-link[data-tab]');
    const tabPanes = document.querySelectorAll('.tab-pane');

    if (navLinks.length > 0 && tabPanes.length > 0) {
        navLinks.forEach(link => {
            link.addEventListener('click', function (e) {
                e.preventDefault();
                const targetTabId = this.getAttribute('data-tab');

                // Update active state on nav links
                navLinks.forEach(nl => nl.classList.remove('active'));
                this.classList.add('active');

                // Show target tab pane
                tabPanes.forEach(pane => {
                    if (pane.id === targetTabId) {
                        pane.classList.add('active');
                    } else {
                        pane.classList.remove('active');
                    }
                });

                // Update URL hash without jumping
                history.pushState(null, null, '#' + targetTabId);

                // Auto-close sidebar on mobile after selection
                if (window.innerWidth <= 992 && sidebar) {
                    sidebar.classList.remove('open');
                }
            });
        });

        // Handle direct URL hash (e.g. #appointments or #profile)
        if (window.location.hash) {
            const hash = window.location.hash.substring(1);
            const targetLink = document.querySelector(`.dash-nav-link[data-tab="${hash}"]`);
            if (targetLink) {
                targetLink.click();
            }
        }
    }

    // 3. Appointment Confirmation Dialogs
    document.querySelectorAll('.confirm-cancel-form').forEach(form => {
        form.addEventListener('submit', function (e) {
            const confirmed = window.confirm("Are you sure you want to cancel this demo appointment? The slot will be released back to other patients.");
            if (!confirmed) {
                e.preventDefault();
            }
        });
    });

    document.querySelectorAll('.confirm-complete-form').forEach(form => {
        form.addEventListener('submit', function (e) {
            const confirmed = window.confirm("Mark this consultation as Completed? This demo status change will be updated in the database.");
            if (!confirmed) {
                e.preventDefault();
            }
        });
    });

    // 4. Copy Booking Reference in Dashboard
    document.querySelectorAll('.btn-copy-ref').forEach(btn => {
        btn.addEventListener('click', function () {
            const ref = this.getAttribute('data-ref');
            if (ref) {
                navigator.clipboard.writeText(ref).then(() => {
                    const original = this.innerHTML;
                    this.innerHTML = '✓ Copied!';
                    setTimeout(() => {
                        this.innerHTML = original;
                    }, 2000);
                });
            }
        });
    });
});

