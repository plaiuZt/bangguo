package com.bangguo.app.model;

import java.util.List;

public class LoginBannerPic {
    private List<ImageAdModel> imgs;
    private List<String> tips;

    public List<ImageAdModel> getImgs() {
        return imgs;
    }

    public void setImgs(List<ImageAdModel> imgs) {
        this.imgs = imgs;
    }

    public List<String> getTips() {
        return tips;
    }

    public void setTips(List<String> tips) {
        this.tips = tips;
    }

    public class ImageAdModel{
        private String imgUrl;
        private String adUrl;

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        public String getAdUrl() {
            return adUrl;
        }

        public void setAdUrl(String adUrl) {
            this.adUrl = adUrl;
        }
    }
}
