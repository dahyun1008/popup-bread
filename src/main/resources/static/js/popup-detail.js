document.addEventListener("DOMContentLoaded", async () => {
    const imagesDiv = document.getElementById("carouselImages");
    const indicator = document.getElementById("carouselIndicator");
    const prevBtn = document.getElementById("prevBtn");
    const nextBtn = document.getElementById("nextBtn");

    if (!imagesDiv || !indicator) {
        console.warn("캐러셀 DOM 없음");
        return;
    }

    const imageKeys = window.popupImageKeys || [];
    if (!imageKeys || imageKeys.length === 0) {
        console.log("이미지 없음, 캐러셀 스킵");
        return;
    }

    try {
        // presigned download URL 요청
        const res = await fetch("/api/images/download-url", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ downloadUrlRequests: imageKeys })
        });

        if (!res.ok) {
            console.error("Presigned URL 요청 실패", res.status);
            return;
        }

        const data = await res.json();
        const successes = data.data?.successes || [];
        const urls = successes.map(s => s.downloadUrl);

        if (urls.length === 0) {
            console.log("다운로드 가능한 이미지 없음");
            return;
        }

        // 이미지 DOM 구성
        imagesDiv.innerHTML = "";
        urls.forEach((url, i) => {
            const img = document.createElement("img");
            img.src = url;
            img.alt = `팝업 이미지 ${i + 1}`;
            imagesDiv.appendChild(img);
        });

        // 캐러셀 상태
        let currentIndex = 0;
        const total = urls.length;
        indicator.textContent = `1 / ${total}`;

        function updateCarousel() {
            imagesDiv.style.transform = `translateX(-${currentIndex * 100}%)`;
            indicator.textContent = `${currentIndex + 1} / ${total}`;
        }

        // 버튼 이벤트
        prevBtn.addEventListener("click", () => {
            if (currentIndex > 0) {
                currentIndex--;
                updateCarousel();
            }
        });

        nextBtn.addEventListener("click", () => {
            if (currentIndex < total - 1) {
                currentIndex++;
                updateCarousel();
            }
        });

        // 마우스 휠 이동
        if (total > 1) {
            imagesDiv.addEventListener("wheel", (e) => {
                if (e.deltaY > 0 && currentIndex < total - 1) {
                    currentIndex++;
                } else if (e.deltaY < 0 && currentIndex > 0) {
                    currentIndex--;
                }
                updateCarousel();
            });
        }
    } catch (err) {
        console.error("이미지 로딩 오류", err);
    }
});
