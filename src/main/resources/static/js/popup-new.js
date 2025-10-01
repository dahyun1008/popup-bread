document.addEventListener("DOMContentLoaded", () => {
    const imageInput = document.getElementById("popupImages");
    const imagePreview = document.getElementById("imagePreview");

    const ALLOWED_EXT = ["jpg", "jpeg", "png", "webp"];
    const MAX_SIZE_MB = 10;
    let selectedFiles = [];

    imageInput.addEventListener("change", async (e) => {
        const files = Array.from(e.target.files);

        if (imagePreview.children.length + files.length > 5) {
            showToast("최대 5장까지 업로드 가능합니다.");
            return;
        }

        for (let file of files) {
            if (imagePreview.children.length >= 5) break;

            let ext = file.name.split(".").pop().toLowerCase();
            let processedFile = file;

            // HEIC → JPEG 변환
            if (ext === "heic") {
                try {
                    const blob = await heic2any({ blob: file, toType: "image/jpeg", quality: 0.9 });
                    processedFile = new File([blob], file.name.replace(/\.heic$/i, ".jpg"), { type: "image/jpeg" });
                    ext = "jpg";
                } catch (err) {
                    console.error(err);
                    showToast("HEIC 변환 실패: " + file.name);
                    continue;
                }
            }

            if (!ALLOWED_EXT.includes(ext)) {
                showToast(`지원하지 않는 파일 형식: ${ext}`);
                continue;
            }
            if (processedFile.size > MAX_SIZE_MB * 1024 * 1024) {
                showToast(`${processedFile.name}은(는) ${MAX_SIZE_MB}MB 이하만 업로드 가능합니다.`);
                continue;
            }

            const reader = new FileReader();
            reader.onload = (ev) => {
                const div = document.createElement("div");
                const img = document.createElement("img");
                const removeBtn = document.createElement("button");

                img.src = ev.target.result;
                removeBtn.textContent = "−";
                removeBtn.classList.add("remove-btn");

                removeBtn.addEventListener("click", () => {
                    div.remove();
                    selectedFiles = selectedFiles.filter(f => f !== processedFile);
                });

                div.appendChild(img);
                div.appendChild(removeBtn);
                imagePreview.appendChild(div);

                selectedFiles.push(processedFile);
            };
            reader.readAsDataURL(processedFile);
        }

        imageInput.value = "";
    });

    // 장소 선택
    const regionBtns = document.querySelectorAll(".region-btn");
    const districtBtns = document.querySelectorAll(".district-btn");
    const districtBox = document.querySelector(".districts");

    let selectedRegion = null;
    let selectedDistrict = null;

    regionBtns.forEach(btn => {
        btn.addEventListener("click", () => {
            regionBtns.forEach(b => b.classList.remove("active"));
            btn.classList.add("active");
            selectedRegion = btn.dataset.region;
            if (districtBox) districtBox.style.display = "flex";
        });
    });

    districtBtns.forEach(btn => {
        btn.addEventListener("click", () => {
            districtBtns.forEach(b => b.classList.remove("active"));
            btn.classList.add("active");
            selectedDistrict = btn.dataset.district;
        });
    });

    // 캘린더 선택 결과 저장
    let selectedDates = [];
    flatpickr("#dateRange", {
        mode: "range",
        dateFormat: "Y-m-d",
        inline: true,
        locale: "ko",
        onClose: (dates) => {
            selectedDates = dates.map(d => d.toISOString().split("T")[0]);
        }
    });

    // 완료 버튼
    document.getElementById("submitBtn").addEventListener("click", async () => {
        const title = document.getElementById("popupTitle").value.trim();
        const detailLocation = document.getElementById("popupDetailLocation").value.trim();

        if (!title || !selectedRegion || selectedDates.length < 2) {
            showToast("필수 정보를 입력하세요!");
            return;
        }

        const presignedReq = {
            uploadUrlRequests: selectedFiles.map(f => ({
                fileName: f.name,
                contentType: f.type,
                uploadType: "popup"
            }))
        };

        let uploadResults = [];
        try {
            const presignedRes = await fetch("/api/images/upload-url", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(presignedReq)
            });

            if (!presignedRes.ok) {
                showToast("이미지 업로드 URL 발급 실패");
                return;
            }

            const presignedData = await presignedRes.json();
            if (presignedData.data.failures?.length > 0) {
                showToast("일부 파일 URL 발급 실패. 다시 시도해주세요.");
                return;
            }

            uploadResults = presignedData.data.successes;

            for (let i = 0; i < selectedFiles.length; i++) {
                const file = selectedFiles[i];
                const uploadUrl = uploadResults[i].uploadUrl;
                const putRes = await fetch(uploadUrl, {
                    method: "PUT",
                    body: file,
                    headers: { "Content-Type": file.type }
                });
                if (!putRes.ok) {
                    showToast(`${file.name} 업로드 실패`);
                    return;
                }
            }
        } catch (err) {
            console.error(err);
            showToast("이미지 업로드 중 오류 발생");
            return;
        }

        const payload = {
            title,
            location: {
                region: selectedRegion,
                district: selectedDistrict,
                detailLocation
            },
            startDate: selectedDates[0],
            endDate: selectedDates[1],
            imageKeys: uploadResults.map(r => r.imageKey),
            coverImageKey: uploadResults[0]?.imageKey
        };

        try {
            const res = await fetch("/api/popups", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(payload)
            });

            if (res.status === 201) {
                showToast("팝업 등록 성공!");
                setTimeout(() => {
                    const locationUrl = res.headers.get("Location");
                    if (locationUrl) window.location.href = locationUrl;
                }, 1500);
            } else if (res.status === 400) {
                const data = await res.json();
                showToast("입력 오류: " + JSON.stringify(data.fieldErrors));
            } else if (res.status === 401) {
                showToast("로그인이 필요합니다.");
                setTimeout(() => window.location.href = "/login", 1000);
            } else if (res.status === 409) {
                showToast("중복된 팝업이 있습니다.");
            } else {
                showToast("등록 실패: " + res.status);
            }
        } catch (err) {
            console.error(err);
            showToast("서버 오류 발생");
        }
    });
});
