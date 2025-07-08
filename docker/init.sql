CREATE INDEX idx_member_profile_id ON member(id);

CREATE INDEX idx_member_coupon_member_id ON member_coupon(member_id);
CREATE INDEX idx_member_coupon_coupon_id ON member_coupon(coupon_id);

CREATE INDEX idx_point_member_id ON point(member_id);

CREATE INDEX idx_coupon_enabled ON coupon(enabled);

CREATE INDEX idx_profile_name ON profile(name);
CREATE INDEX idx_profile_view_count ON profile(view_count);
CREATE INDEX idx_profile_created_at ON profile(created_at);
