package com.example.inventory_management.entity;

/**
 * حالات دورة حياة المنتج (workflow).
 * الانتقالات المسموحة تتحكم فيها قاعدة العمل في ProductServiceImpl.
 *   ACTIVE       -> DISCONTINUED
 *   DISCONTINUED -> ACTIVE أو ARCHIVED
 *   ARCHIVED     -> (حالة نهائية، ما فيه رجوع)
 */
public enum ProductStatus {
    ACTIVE,
    DISCONTINUED,
    ARCHIVED
}