# Manual Testing Scenarios for University Vehicle Tracking System

## Overview
This document provides comprehensive manual testing scenarios to validate the complete user journey and system functionality. These tests should be performed in addition to automated tests to ensure real-world usability.

## Prerequisites
- Backend server running on `http://localhost:8080`
- Frontend application running on `http://localhost:3000`
- Demo data loaded (should happen automatically on first startup)

## Test Scenarios

### 1. Authentication Flow Testing

#### 1.1 Login Validation
**Objective**: Verify login functionality for all user roles

**Test Steps**:
1. Navigate to `http://localhost:3000`
2. Verify redirect to login page
3. Test each demo account:
   - Admin: `admin@university.edu` / `admin123`
   - Driver: `driver1@university.edu` / `driver123`
   - Student: `student1@university.edu` / `student123`
   - Teacher: `teacher1@university.edu` / `teacher123`
   - Office Admin: `office_admin@university.edu` / `office123`

**Expected Results**:
- Each login should succeed and redirect to appropriate dashboard
- JWT token should be stored in localStorage
- User information should display correctly
- Role-based navigation should be visible

#### 1.2 Login Error Handling
**Test Steps**:
1. Try invalid username: `invalid@test.com` / `password123`
2. Try invalid password: `admin@university.edu` / `wrongpassword`
3. Try empty fields
4. Try malformed email addresses

**Expected Results**:
- Appropriate error messages displayed
- No successful login
- Form validation messages appear
- No system crashes or errors

#### 1.3 Session Management
**Test Steps**:
1. Login successfully
2. Wait for token expiration (or manually delete token from localStorage)
3. Try to access protected routes
4. Refresh the page

**Expected Results**:
- Expired sessions redirect to login
- Protected routes are inaccessible without valid token
- Page refresh maintains session if token is valid

---

### 2. Dashboard Functionality Testing

#### 2.1 Role-Based Dashboard Access
**Objective**: Verify each role sees appropriate dashboard content

**Test Steps for Each Role**:
1. Login as each role
2. Verify dashboard content matches role permissions
3. Test navigation menu items
4. Verify statistics and data display

**Expected Results**:
- Admin: Full access to all features, user management, vehicle management
- Driver: Vehicle tracking, location updates, assigned vehicle info
- Student/Teacher: Vehicle tracking, chat system
- Office Admin: Administrative vehicle access

#### 2.2 Real-Time Data Updates
**Test Steps**:
1. Login as Admin
2. Navigate to vehicle tracking
3. Open another browser/incognito window
4. Login as Driver (driver1)
5. Update location from driver account
6. Verify updates appear on admin dashboard

**Expected Results**:
- Location updates appear in real-time
- Auto-refresh functionality works
- Data consistency across sessions

---

### 3. Vehicle Management Testing

#### 3.1 Vehicle Creation (Admin Only)
**Test Steps**:
1. Login as Admin
2. Navigate to vehicle management (if available)
3. Create new vehicle with valid data:
   - Vehicle Number: `STU-999`
   - Model: `Test Bus Model`
   - Brand: `Test Brand`
   - Capacity: `45`
   - Type: `STUDENT_BUS`
   - University: `Test University`
4. Assign available driver

**Expected Results**:
- Vehicle created successfully
- Vehicle appears in vehicle list
- Driver assignment works correctly
- Validation prevents duplicate vehicle numbers

#### 3.2 Vehicle Number Validation
**Test Steps**:
1. Try creating vehicle with invalid numbers:
   - `INVALID-001` (invalid prefix)
   - `STU001` (missing hyphen)
   - `STU-0001` (too many digits)
   - `TEST-001` (test pattern)

**Expected Results**:
- Validation errors displayed
- Vehicle not created with invalid numbers
- Clear error messages guide user

#### 3.3 Driver Assignment
**Test Steps**:
1. Create vehicle without driver
2. Assign driver to vehicle
3. Try assigning same driver to another vehicle
4. Unassign driver from vehicle

**Expected Results**:
- Driver assignment updates correctly
- Cannot assign driver to multiple vehicles
- Driver information displays correctly
- Unassignment works properly

---

### 4. Real-Time Location Tracking Testing

#### 4.1 Location Update Flow
**Test Steps**:
1. Login as Driver (driver1)
2. Navigate to "My Vehicle" or location update interface
3. Update location with valid coordinates:
   - Latitude: `40.7589`
   - Longitude: `-73.9851`
   - Speed: `25.0`
   - Direction: `North`
   - Fuel Level: `80.0`

**Expected Results**:
- Location update accepts valid GPS coordinates
- Speed and direction validation works
- Fuel level updates correctly
- Timestamp updates automatically

#### 4.2 Location Validation
**Test Steps**:
1. Try updating with invalid coordinates:
   - Latitude: `91.0` (invalid range)
   - Longitude: `-181.0` (invalid range)
   - Speed: `-10.0` (negative speed)
   - Fuel Level: `150.0` (over 100%)

**Expected Results**:
- Validation errors for invalid ranges
- Location not updated with invalid data
- Clear error messages displayed

#### 4.3 Real-Time Display
**Test Steps**:
1. Login as Admin in one browser
2. Login as Driver in another browser
3. Update driver location multiple times
4. Monitor admin dashboard for updates

**Expected Results**:
- Updates appear within 30 seconds (auto-refresh)
- Coordinate accuracy maintained
- Last update time displayed correctly
- Status indicators work properly

---

### 5. Vehicle Tracking Interface Testing

#### 5.1 Vehicle List Display
**Test Steps**:
1. Login as any role
2. Navigate to "Vehicle Tracking"
3. Verify all demo vehicles displayed
4. Check data completeness for each vehicle

**Expected Results**:
- All active vehicles displayed
- Vehicle information complete (number, model, driver, etc.)
- Status indicators show correctly
- Fuel level bars display properly
- Last update times formatted correctly

#### 5.2 Filtering and Search
**Test Steps**:
1. Test vehicle type filtering (if available)
2. Test university filtering
3. Test search functionality
4. Test status filtering

**Expected Results**:
- Filters work correctly
- Search returns relevant results
- Multiple filters can be applied
- Clear filter functionality works

#### 5.3 Auto-Refresh Functionality
**Test Steps**:
1. Note current data and timestamp
2. Wait for 30-second auto-refresh
3. Verify data updates and timestamp changes
4. Monitor for any errors or failures

**Expected Results**:
- Auto-refresh works every 30 seconds
- Timestamp updates correctly
- No API errors during refresh
- Data consistency maintained

---

### 6. Security Testing

#### 6.1 Authorization Testing
**Test Steps**:
1. Login as Student
2. Try accessing admin-only URLs directly:
   - `/admin`
   - `/api/vehicles` (POST request)
   - `/api/users`

**Expected Results**:
- Access denied for unauthorized endpoints
- Proper error messages displayed
- No sensitive data exposed
- Redirects work correctly

#### 6.2 JWT Token Security
**Test Steps**:
1. Login successfully
2. Inspect localStorage for JWT token
3. Decode JWT payload (using jwt.io)
4. Modify token and try accessing protected routes

**Expected Results**:
- Token contains appropriate user information
- No sensitive data in token payload
- Modified tokens are rejected
- Expired tokens redirect to login

#### 6.3 API Security
**Test Steps**:
1. Use browser dev tools or Postman
2. Try API calls without authentication
3. Try API calls with invalid tokens
4. Try API calls with wrong role permissions

**Expected Results**:
- Unauthenticated requests return 401
- Invalid tokens return 401
- Insufficient permissions return 403
- Error messages don't expose system details

---

### 7. Error Handling and Edge Cases

#### 7.1 Network Error Handling
**Test Steps**:
1. Disconnect internet/stop backend server
2. Try various operations in frontend
3. Reconnect and verify recovery

**Expected Results**:
- Graceful error messages displayed
- No application crashes
- Proper retry mechanisms
- Recovery works when connection restored

#### 7.2 Data Validation Edge Cases
**Test Steps**:
1. Try extremely long text inputs
2. Try special characters in inputs
3. Try XSS attempts in text fields
4. Try SQL injection attempts

**Expected Results**:
- Input length limits enforced
- Special characters handled properly
- XSS attempts blocked/sanitized
- SQL injection prevented

#### 7.3 Concurrent User Testing
**Test Steps**:
1. Open multiple browser windows
2. Login as different users simultaneously
3. Perform operations concurrently
4. Verify data consistency

**Expected Results**:
- Multiple users can work simultaneously
- Data updates don't conflict
- Real-time updates work for all users
- No data corruption occurs

---

### 8. Performance and Usability Testing

#### 8.1 Page Load Performance
**Test Steps**:
1. Clear browser cache
2. Measure page load times for:
   - Login page
   - Dashboard
   - Vehicle tracking page
3. Test with browser dev tools throttling

**Expected Results**:
- Pages load within 2-3 seconds
- Reasonable performance on slow connections
- Progressive loading for large datasets
- No blocking operations

#### 8.2 Mobile Responsiveness
**Test Steps**:
1. Test on mobile devices or browser mobile view
2. Verify all functionality works on mobile
3. Test touch interactions
4. Verify readable text and proper sizing

**Expected Results**:
- All pages responsive on mobile
- Navigation works on touch devices
- Text remains readable
- No horizontal scrolling required

#### 8.3 Accessibility Testing
**Test Steps**:
1. Test keyboard navigation
2. Test with screen reader simulation
3. Verify color contrast
4. Test with disabled JavaScript

**Expected Results**:
- Full keyboard navigation support
- Screen reader compatibility
- Sufficient color contrast
- Graceful degradation without JavaScript

---

### 9. Data Integrity Testing

#### 9.1 Data Persistence
**Test Steps**:
1. Create/update data
2. Restart backend server
3. Verify data persists
4. Check data consistency

**Expected Results**:
- All data persists across restarts
- No data corruption
- Relationships maintained
- Timestamps preserved

#### 9.2 Concurrent Data Updates
**Test Steps**:
1. Login as multiple users
2. Update same vehicle data simultaneously
3. Verify conflict resolution
4. Check data consistency

**Expected Results**:
- Last update wins or proper conflict resolution
- No data corruption
- Appropriate error messages
- Data consistency maintained

---

## Test Execution Checklist

### Before Testing
- [ ] Backend server running and accessible
- [ ] Frontend application running
- [ ] Demo data loaded successfully
- [ ] Test browsers/devices available
- [ ] Network connectivity verified

### During Testing
- [ ] Document all bugs and issues
- [ ] Take screenshots of problems
- [ ] Note browser/device for each test
- [ ] Record performance metrics
- [ ] Test both happy path and error cases

### After Testing
- [ ] Summarize test results
- [ ] Prioritize bugs by severity
- [ ] Verify fixes with retesting
- [ ] Document any workarounds needed
- [ ] Update test scenarios based on findings

## Test Data Reset

If you need to reset test data during testing:

1. **Backend Reset**:
   ```bash
   # Stop backend server
   # Restart server (H2 database will reset automatically)
   cd backend
   ./run.sh  # or run.bat on Windows
   ```

2. **Frontend Reset**:
   ```bash
   # Clear browser cache and localStorage
   # Refresh application
   ```

## Reporting Issues

When reporting issues, include:
- Test scenario number and step
- Expected vs actual behavior
- Browser/device information
- Screenshots or error messages
- Steps to reproduce
- Data state when issue occurred

---

**Note**: This manual testing should be performed regularly, especially after:
- Code changes
- Dependency updates
- Environment changes
- Before releases 