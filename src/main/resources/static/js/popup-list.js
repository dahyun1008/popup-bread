document.addEventListener("DOMContentLoaded", () => {
    const buttons = document.querySelectorAll(".region-btn");
    const popupGrid = document.getElementById("popupGrid");
    let selectedRegion = null;

    /**
     * presigned URL 발급 → <img> 태그에 적용
     */
    async function applyPresignedUrls(cards) {
        const keys = Array.from(cards)
            .map(card => card.dataset.coverKey)
            .filter(k => !!k); // coverKey 없는 건 제외

        if (keys.length === 0) return;

        try {
            const res = await fetch("/api/images/download-url", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ downloadUrlRequests: keys })
            });

            if (!res.ok) {
                console.error("다운로드 URL 요청 실패");
                return;
            }

            const data = await res.json();
            const map = {};
            (data.data?.successes || []).forEach(s => {
                map[s.imageKey] = s.downloadUrl;
            });

            cards.forEach(card => {
                const key = card.dataset.coverKey;
                const img = card.querySelector("img");

                if (!key) {
                    // cover 이미지가 아예 없는 경우 → bread.jpg 유지
                    img.src = "/images/bread.jpg";
                } else if (map[key]) {
                    img.src = map[key]; // presigned URL로 교체
                } else {
                    img.src = "/images/bread.jpg"; // 실패 시 기본 이미지
                }
            });
        } catch (err) {
            console.error("Presigned URL 처리 오류", err);
        }
    }

    /**
     * 팝업 목록 불러오기
     */
    async function loadPopups(region) {
        let url = `/api/popups?limit=6`;
        if (region) {
            url += `&region=${encodeURIComponent(region)}`;
        }

        try {
            const response = await fetch(url);
            if (!response.ok) throw new Error("네트워크 오류");

            const result = await response.json();
            popupGrid.innerHTML = "";

            if (result.data.length === 0) {
                popupGrid.innerHTML = "<p style='padding:16px;'>진행중인 팝업이 없습니다.</p>";
                return;
            }

            result.data.forEach(popup => {
                const card = document.createElement("a");
                card.href = `/popups/${popup.popupId}`;
                card.className = "popup-card";

                if (popup.coverImageUrl) {
                    card.dataset.coverKey = popup.coverImageUrl;
                }

                card.innerHTML = `
                    <img src="/images/bread.jpg" alt="팝업 이미지">
                    <div class="info">
                        <h2>${popup.title}</h2>
                        <p>${popup.place}</p>
                        <p>${popup.startDate} ~ ${popup.endDate}</p>
                    </div>
                `;
                popupGrid.appendChild(card);
            });

            // CSR 결과도 presigned URL 적용
            const newCards = popupGrid.querySelectorAll(".popup-card");
            await applyPresignedUrls(newCards);
        } catch (error) {
            console.error("팝업 조회 실패:", error);
        }
    }

    // 필터 버튼 이벤트
    buttons.forEach(btn => {
        btn.addEventListener("click", () => {
            const region = btn.dataset.region;

            if (selectedRegion === region) {
                btn.classList.remove("active");
                selectedRegion = null;
                loadPopups(null);
            } else {
                buttons.forEach(b => b.classList.remove("active"));
                btn.classList.add("active");
                selectedRegion = region;
                loadPopups(region);
            }
        });
    });

    // SSR 렌더링된 카드들도 presigned URL 교체
    const ssrCards = popupGrid.querySelectorAll(".popup-card");
    applyPresignedUrls(ssrCards);

    // 초기 CSR 로드 (추가 데이터 로드 목적)
    loadPopups(null);
});
