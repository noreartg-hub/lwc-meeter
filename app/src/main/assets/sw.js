// ================================================================
//  LWC Meter — Service Worker
//  يتيح العمل بدون إنترنت وتحديث سريع
// ================================================================
const CACHE    = 'lwc-v1.0.0';
const OFFLINE  = ['/index.html', '/manifest.json'];

// تثبيت: حفظ الملفات الأساسية
self.addEventListener('install', e => {
    e.waitUntil(
        caches.open(CACHE).then(c => c.addAll(OFFLINE))
    );
    self.skipWaiting();
});

// تنشيط: حذف الكاش القديم
self.addEventListener('activate', e => {
    e.waitUntil(
        caches.keys().then(keys =>
            Promise.all(keys.filter(k => k !== CACHE).map(k => caches.delete(k)))
        )
    );
    self.clients.claim();
});

// جلب: cache-first للملفات، network-first للـ API
self.addEventListener('fetch', e => {
    const url = new URL(e.request.url);

    // API calls: network first
    if (url.pathname.startsWith('/api/')) {
        e.respondWith(
            fetch(e.request)
                .then(res => {
                    if (res.ok) {
                        const clone = res.clone();
                        caches.open(CACHE).then(c => c.put(e.request, clone));
                    }
                    return res;
                })
                .catch(() => caches.match(e.request))
        );
        return;
    }

    // Static files: cache first
    e.respondWith(
        caches.match(e.request).then(cached => cached || fetch(e.request))
    );
});

// Push Notifications
self.addEventListener('push', e => {
    const data = e.data?.json() || {};
    e.waitUntil(
        self.registration.showNotification(data.title || 'LWC Meter', {
            body:    data.body  || '',
            icon:    '/icons/icon-192.png',
            badge:   '/icons/icon-96.png',
            vibrate: [200, 100, 200],
            data:    { url: data.url || '/' },
            actions: [
                { action: 'open',    title: 'فتح التطبيق' },
                { action: 'dismiss', title: 'تجاهل'       },
            ]
        })
    );
});

self.addEventListener('notificationclick', e => {
    e.notification.close();
    if (e.action !== 'dismiss') {
        e.waitUntil(clients.openWindow(e.notification.data?.url || '/'));
    }
});
