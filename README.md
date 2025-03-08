# Proje: proje.shop
# [Proje Canlı Link: proje.shop](https://proje.shop)

## Hakkında
proje.shop, güvenli bir kullanıcı yönetim sistemi sunan bir e-ticaret sitesidir. Kullanıcıların hesap güvenliğini sağlamak amacıyla çeşitli güvenlik önlemleri entegre edilmiştir.

## Özellikler
```
- Kullanıcı Girişi ve Kayıt Olma
- Şifremi Unuttum: OTP kodu ile şifre sıfırlama
- Hesap Aktivasyonu: E-posta doğrulama
- Oturum Aktivasyonu: Cihaz/IP değişiminde OTP ile doğrulama
- Hatalı Girişlerde Hesap Blokesi
- Onaysız Oturum Denemelerinde IP Blokesi
- Güvenli Şifre Yönetimi
```

## Kurulum
```
1. Projeyi klonlayın:
   git clone https://github.com/kullanici/proje-shop.git

2. Bağımlılıkları yükleyin:
   npm install

3. Backend'i başlatın:
   mvn spring-boot:run

4. Frontend'i başlatın:
   npm run dev
```

## Kullanım
```
- Kullanıcılar kayıt olduktan sonra e-posta aktivasyonu yapmalıdır.
- Giriş yaparken yeni bir cihaz/IP algılandığında OTP kodu ile doğrulama gereklidir.
- Şifresini unutan kullanıcılar, OTP kodu ile şifre sıfırlayabilir.
- Çok fazla hatalı giriş yapılırsa, hesap geçici olarak bloke edilir.
```

## Güvenlik Önlemleri
```
- JWT tabanlı kimlik doğrulama
- Oturum süresini Refresh Token ve Access Token ile kontrol etme
- IP ve cihaz takibi
- Şüpheli oturumlarda OTP doğrulama
- Güçlü şifre politikası
- E-posta doğrulama
```

## Barındırma
```
- **Web Hosting:** Frontend (React) bir web hosting üzerinde barındırılmaktadır.
- **Veritabanı:** Web hosting üzerindeki veritabanında saklanmaktadır.
- **API:** Backend (Spring Boot) bir Linux VDS sunucusunda çalışmaktadır.
```

## Lisans
```
Bu proje MIT lisansı ile dağıtılmaktadır.
```

